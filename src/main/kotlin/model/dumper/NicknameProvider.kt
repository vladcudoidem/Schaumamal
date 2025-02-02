package model.dumper

class NicknameProvider {
    fun getNext(current: String?): String {
        current ?: return nicknames.first()

        val currentIndex = nicknames.indexOf(current)
            .also {
                if (it == -1) return nicknames.first()
            }
        val nextIndex = (currentIndex + 1) % nicknames.size

        return nicknames[nextIndex]
    }

    companion object {
        val nicknames = listOf(
            "Frodo", "Sam", "Merry", "Pippin", "Aragorn", "Legolas", "Gimli", "Boromir",
            "Gandalf", "Gollum", "Sauron", "Elrond", "Galadriel", "Arwen", "Faramir",
            "Théoden", "Eomer", "Eowyn", "Isildur", "Radagast", "Saruman", "Thorin",
            "Smaug", "Beorn", "Glorfindel", "Zeus", "Hera", "Ares", "Apollo", "Athena",
            "Artemis", "Hermes", "Hades", "Perseus", "Achilles", "Hector", "Medusa",
            "Circe", "Pandora", "Theseus", "Minos", "Demeter", "Orpheus", "Icarus",
            "Eurydice", "Thor", "Odin", "Loki", "Freya", "Tyr", "Balder", "Fenrir",
            "Jörmungandr", "Heimdall", "Skadi", "Frigg", "Njord", "Aegir", "Valkyrie",
            "Hel", "Bragi", "Mimir", "Sif", "Eir", "Țepeș", "Viteazul", "Brâncoveanu",
            "Bălcescu", "Cantemir", "Eminescu", "Iorga", "Eliade", "Coandă", "Kogălniceanu",
            "Caragiale", "Creangă"
        )
    }
}