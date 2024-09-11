package de.larmic.kotlindsltalk.slides


import java.time.LocalDate
import java.time.Month

class Company {
    var name: String = ""
    var city: String = ""
    var employees: Int = 0
}

class Social {
    var twitter: String = ""
    var linkedIn: String = ""
}

class Speaker {
    var name: String = ""
    var email: String = ""
    var jobTitle: String = ""
    var company: Company = Company()
    var social: Social = Social()

    fun company(init: Company.() -> Unit) {
        company.init()
    }

    fun social(init: Social.() -> Unit) {
        social.init()
    }
}

enum class Conference {
    KKON, JFN, JAX, JAVA_LAND
}

class Presentation {
    var title: String = ""
    var date: LocalDate = LocalDate.now()
    var conference: Conference = Conference.KKON
    var speaker: Speaker = Speaker()
    var github: String = ""

    fun speaker(init: Speaker.() -> Unit) {
        speaker.init()
    }
}

fun presentation(init: Presentation.() -> Unit): Presentation {
    val presentation = Presentation()
    presentation.init()
    return presentation
}

infix fun Int.SEPTEMBER(year: Int) = LocalDate.of(year, Month.SEPTEMBER, this)