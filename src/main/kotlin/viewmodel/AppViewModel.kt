package viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
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
import model.repository.dataClasses.Settings
import oldModel.notification.Notification
import oldModel.notification.NotificationManager

class AppViewModel(
    val notificationManager: NotificationManager,
    private val dumper: Dumper,
    private val appRepository: AppRepository,
    private val displayDataResolver: DisplayDataResolver,
    private val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    stateCollectionScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    init {
        appRepository.createAppDirectory()
    }

    private val _state = MutableStateFlow(InspectorState.EMPTY)
    val state get() = _state.asStateFlow()

    private val content = MutableStateFlow(Content.DefaultEmpty)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            content.collect {
                println("1 content=${content.value}")
            }
        }
    }

    init {
        if (appRepository.existsContentJson()) {
            content.value = appRepository.readContentJson()
            _state.value = InspectorState.POPULATED
        } else {
            appRepository.writeContentJson(content.value)
        }

        appRepository.createContentDirectories(content.value)
    }

    private val settings = MutableStateFlow(Settings.DefaultEmpty)

    init {
        if (appRepository.existsSettingsJson()) {
            settings.value = appRepository.readSettingsJson()
        } else {
            appRepository.writeSettingsJson(settings.value)
        }
    }

    private val dumpDisplaysData = content
        .filter {
            it.dumps.isNotEmpty() && it.dumps.first().displays.isNotEmpty()
        }
        .map {
            println("content=${content.value}")
            val dumpsDirectoryName = it.dumpsDirectoryName

            // Todo: offer option to choose older dumps.
            val selectedDump = it.dumps.first()

            println("dumpsDirectoryName=$dumpsDirectoryName, selectedDump=$selectedDump")
            displayDataResolver.resolve(dumpsDirectoryName, selectedDump)
        }

    private val dumpDaisplaysData = content.transform {
        println("content=${content.value}")
        val dumpsDirectoryName = it.dumpsDirectoryName

        // Todo: offer option to choose older dumps.
        val selectedDump = it.dumps.firstOrNull()

        if (selectedDump != null && selectedDump.displays.isNotEmpty()) {
            println("dumpsDirectoryName=$dumpsDirectoryName, selectedDump=$selectedDump")
            emit(displayDataResolver.resolve(dumpsDirectoryName, selectedDump))
        }
    }

    private val _displayIndex = MutableStateFlow(0)
    val displayIndex get() = _displayIndex.asStateFlow()

    val displayCount =
        dumpDisplaysData
            .map { it.size }
            .stateIn(
                scope = stateCollectionScope,
                started = SharingStarted.Eagerly,
                initialValue = 0
            )

    // Todo: refactor to "selctedDisplayData"
    val data =
        combine(dumpDisplaysData, _displayIndex) { dumpDisplaysData, _displayIndex ->
            dumpDisplaysData[_displayIndex]
        }.stateIn(
            scope = stateCollectionScope,
            started = SharingStarted.Eagerly,
            initialValue = DisplayData.Empty
        )

    private val _isNodeSelected = MutableStateFlow(false)
    val isNodeSelected get() = _isNodeSelected.asStateFlow()

    private val _selectedNode = MutableStateFlow(GenericNode.Empty)
    val selectedNode get() = _selectedNode.asStateFlow()

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

            _isNodeSelected.value = false
            _selectedNode.value = GenericNode.Empty

            _displayIndex.value = 0

            appRepository.writeContentJson(newContent)
            content.value = newContent

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
        when (direction) {
            Direction.NEXT ->
                _displayIndex.update { it.inc().coerceAtMost(displayCount.value - 1) }

            Direction.PREVIOUS -> _displayIndex.update { it.dec().coerceAtLeast(0) }
        }
    }
}