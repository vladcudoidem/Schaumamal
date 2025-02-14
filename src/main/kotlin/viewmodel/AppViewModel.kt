package viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.InspectorState
import model.displayDataResolver.DisplayData
import model.displayDataResolver.DisplayDataResolver
import model.dumper.DumpResult
import model.dumper.Dumper
import model.parser.dataClasses.GenericNode
import model.repository.AppRepository
import model.repository.DumpRegisterResult
import model.repository.dataClasses.Content
import model.repository.dataClasses.Dump
import model.repository.dataClasses.Settings
import viewmodel.notification.Notification
import viewmodel.notification.NotificationManager

class AppViewModel(
    val notificationManager: NotificationManager,
    private val dumper: Dumper,
    private val appRepository: AppRepository,
    private val displayDataResolver: DisplayDataResolver,
    private val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    stateCollectionScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _state = MutableStateFlow(InspectorState.EMPTY)
    val state get() = _state.asStateFlow()

    private val content = MutableStateFlow(Content.DefaultEmpty)
    private val settings = MutableStateFlow(Settings.DefaultEmpty)

    private val dumpsDirectoryName = content.map { it.dumpsDirectoryName }
    val resolvedDumpThumbnails = content.map {
        displayDataResolver.resolve(it.dumpsDirectoryName, it.dumps)
    }

    private val _selectedDump = MutableStateFlow(Dump.Empty)
    val selectedDump get() = _selectedDump.asStateFlow()

    private val displayDataList =
        combineTransform(dumpsDirectoryName, _selectedDump) { dumpsDirectoryName, selectedDump ->
            if (selectedDump != Dump.Empty && selectedDump.displays.isNotEmpty()) {
                emit(displayDataResolver.resolve(dumpsDirectoryName, selectedDump))
            }
        }.stateIn(
            scope = stateCollectionScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf()
        )

    private val _displayIndex = MutableStateFlow(0)
    val displayIndex get() = _displayIndex.asStateFlow()

    val displayCount =
        displayDataList
            .map { it.size }
            .stateIn(
                scope = stateCollectionScope,
                started = SharingStarted.Eagerly,
                initialValue = 0
            )

    val selectedDisplayData =
        combineTransform(displayDataList, _displayIndex) { dumpDisplaysData, _displayIndex ->
            if (dumpDisplaysData.isNotEmpty()) emit(dumpDisplaysData[_displayIndex])
        }.stateIn(
            scope = stateCollectionScope,
            started = SharingStarted.Eagerly,
            initialValue = DisplayData.Empty
        )

    private val _isNodeSelected = MutableStateFlow(false)
    val isNodeSelected get() = _isNodeSelected.asStateFlow()

    private val _selectedNode = MutableStateFlow(GenericNode.Empty)
    val selectedNode get() = _selectedNode.asStateFlow()

    init {
        appRepository.createAppDirectory()

        // Set up content.
        if (appRepository.existsContentJson()) {
            content.value = appRepository.readContentJson()
            _selectedDump.value = content.value.dumps.first()
            _state.value = InspectorState.POPULATED
        } else {
            appRepository.writeContentJson(content.value)
        }

        appRepository.createContentDirectories(content.value)

        // Set up settings.
        if (appRepository.existsSettingsJson()) {
            settings.value = appRepository.readSettingsJson()
        } else {
            appRepository.writeSettingsJson(settings.value)
        }
    }

    fun extract() {
        viewModelScope.launch {
            val previousStateValue = _state.getAndUpdate { InspectorState.WAITING }

            val dumpResult = dumper.dump(
                lastNickname = content.value.dumps.firstOrNull()?.nickname,
                tempDirectoryName = content.value.tempDirectoryName,
            )

            val newDump = when (dumpResult) {
                is DumpResult.Error -> {
                    notificationManager.notify(
                        Notification(description = dumpResult.reason)
                    )

                    _state.value = previousStateValue
                    return@launch
                }

                is DumpResult.Success -> dumpResult.dump
            }

            val registerResult = appRepository.registerNewDump(
                dump = newDump,
                content = content.value,
                maxDumps = settings.value.maxDumps
            )

            val newContent = when (registerResult) {
                is DumpRegisterResult.Error -> {
                    notificationManager.notify(
                        Notification(description = registerResult.reason)
                    )

                    _state.value = previousStateValue
                    return@launch
                }

                is DumpRegisterResult.Success -> registerResult.content
            }

            // Remove selected node for new dump.
            _isNodeSelected.value = false
            _selectedNode.value = GenericNode.Empty

            // Always show the first display of new dump.
            _displayIndex.value = 0

            // Update the content JSON.
            appRepository.writeContentJson(newContent)

            content.value = newContent

            // Always show the new dump after dumping. (The newest is the first in the list.)
            _selectedDump.value = content.value.dumps.first()

            // At the end, show the data.
            _state.value = InspectorState.POPULATED
        }
    }

    fun selectNode(node: GenericNode) {
        _selectedNode.value = node
        // Shows selected node only after refreshing the data.
        _isNodeSelected.value = true
    }

    fun switchDisplay(direction: Direction) {
        var changed = false

        _displayIndex.update {
            when (direction) {
                Direction.PREVIOUS -> {
                    val newValue = it.dec()

                    if (it > 0) {
                        changed = true
                        newValue
                    } else {
                        it
                    }
                }

                Direction.NEXT -> {
                    val newValue = it.inc()

                    if (it < displayCount.value - 1) {
                        changed = true
                        newValue
                    } else {
                        it
                    }
                }
            }
        }

        if (changed) {
            _isNodeSelected.value = false
            _selectedNode.value = GenericNode.Empty
        }
    }

    fun selectDump(dump: Dump) {
        _selectedDump.value = dump
    }

    fun cleanup() {
        viewModelScope.cancel()
    }
}