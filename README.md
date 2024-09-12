# An introduction into Kotlin DSL

This is a conference talk (in german). 

## Abstract

Kotlin eignet sich hervorragend zur Erstellung domain-spezifischer Sprachen (DSLs). Mit Kotlin DSL können Entwickler*innen die Testlogik klar und präzise ausdrücken, was zu besser lesbarem, leichter wartbarem und insgesamt effizienterem Code führt. So kann der der Testcode sogar mit wenig oder gar keiner Erfahrung in Kotlin gelesen und bearbeitet werden, sodass er für das gesamte Entwicklungsteam zugänglicher und die Zusammenarbeit zwischen Entwicklerinnen und Testern gefördert wird. In diesem Vortrag gibt Lars Michaelis eine grundlegende Einführung in Kotlin DSL und zeigt deren praktischen Nutzen. Er demonstriert am Beispiel einer Testanwendung mit Spring Boot, wie es mit Kotlin DSL gelingt, Tests auf intuitive und deklarative Weise zu schreiben, und wie diese Tests mit Spring Boot zusammenarbeiten können.

## Goals

In diesem Vortrag lernst Du:
* was Kotlin DSL ausmacht und welche Bedeutung es für die Entwicklung von domain-spezifischen Sprachen hat
* die praktische Anwendung von Kotlin DSL anhand einer Testanwendung mit Spring Boot
* wie Kotlin DSL es ermöglicht, Tests intuitiv und deklarativ zu gestalten

## Script

### DSL

#### Warum ist eine DSL

Definition: Eine Domain-Specific Language ist eine Sprache, die speziell für eine bestimmte Anwendungsdomäne entwickelt wurde.  
Beispiele: HTML, SQL, Gradle, ...

* Erhöhte Produktivität und Lesbarkeit.
* Fokussierung auf das Problem statt auf Implementierungsdetails.
* Vereinfachung der Kommunikation zwischen Entwicklern und Domänenexperten.

#### Vorstellung des Projekts

* Erklären CompanyRepository.kt
* Erklären CompanyController.kt inkl. CompanyControllerTest.kt

#### Infix

-> CompanyRepository#exits() mit infix   
--> Aufruf im CompanyController.kt anpassen

#### Extension Functions

-> fluent Api im Controller
```kotlin
fun create(@RequestBody dto: CreateCompanyDto) = dto
        .mapToEntity()
        .storyInDatabase()
        .mapToDto()

fun readTweet(@PathVariable id: Long): ResponseEntity<ReadCompanyDto> {
    if (companyRepository exists id) {
        return companyRepository.get(id).wrapInResponse()
    }

    return ResponseEntity.notFound().build()
}
```

-> Test des Controllers  
--> DTO Factory mit Extension Functions

#### Type Inference

#### Lambda with Receiver

