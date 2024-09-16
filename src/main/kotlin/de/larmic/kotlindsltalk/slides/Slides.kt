package de.larmic.kotlindsltalk.slides

import de.larmic.kotlindsltalk.slides.Conference.KKON
import java.time.LocalDate
import java.time.Month

val p = presentation {
    title = "Eine Einführung in Kotlin DSL"
    conference = KKON
    date = 16 SEPTEMBER 2024

    github = "https://github.com/larmic/kotlin-dsl-talk"

    speaker {
        name = "Lars Michaelis"
        email = "l.michaelis@neusta.de"
        jobTitle = "Solution Architect, Technical Coach"

        social {
            linkedIn = "https://www.linkedin.com/in/lars-michaelis-6778761b3/"
            twitter = "https://twitter.com/larmicDE"
        }

        company {
            name = "Team Neusta"
            city = "Bremen"
            employees = 1400
        }
    }
}

fun `presentation`() {
    val company = Company()
    company.name = "Team Neusta"
    company.city = "Bremen"
    company.employees = 1400

    val social = Social()
    social.linkedIn = "https://www.linkedin.com/in/lars-michaelis-6778761b3/"
    social.twitter = "https://twitter.com/larmicDE"

    val speaker = Speaker()
    speaker.name = "Lars Michaelis"
    speaker.email = "l.michaelis@neusta.de"
    speaker.jobTitle = "Solution Architect, Technical Coach"
    speaker.social = social
    speaker.company = company

    val presentation = Presentation()
    presentation.title = "Eine Einführung in Kotlin DSL"
    presentation.conference = KKON
    presentation.date = LocalDate.of(2024, Month.SEPTEMBER, 16)
    presentation.github = "https://github.com/larmic/kotlin-dsl-talk"
    presentation.speaker = speaker
}