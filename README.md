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

Erlaubt es, Klassen nachträglich zu erweitern und macht den Code lesbarer.

-> fluent Api im Controller
```kotlin
fun create(@RequestBody dto: CreateCompanyDto) : ReadCompanyDto = dto
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

#### Type Inference

Keine Angabe des Type: Macht die DSLs kompakter und intuitiver.

```kotlin
var name : String = "this is a string"
var name = "this is a string"

// kein expliziter Rückgabewert angegeben
fun create(@RequestBody dto: CreateCompanyDto) = dto
        .mapToEntity()
        .storyInDatabase()
        .mapToDto()
```

#### Lambda with Receiver

-> Test des Controllers  
--> DTO Factory mit Lambda with receiver

Was ich gerne hätte
```kotlin
content = body {
    name = "Panzerknacker AG"
    employee {
        name = "Karlchen Knack"
        email = "karlchen@knack.de"
    }
    employee {
        name = "Kuno Knack"
        email = "kuno@knack.de"
    }
}
```

Erzeugen einer TestDataFactory.kt

1. Builder erzeugen
```kotlin
// einfacher Builder
class CreateEmployeeDtoBuilder {
    var name: String = "Donald Duck"
    var email: String = "donald@duck.de"

    fun build() = CreateEmployeeDto(name = name, email = email)
}

class CreateCompanyDtoBuilder(private val employees: MutableList<CreateEmployeeDto> = mutableListOf()) {
    var name: String = "Entenhausen AG"

    fun buildJson() = jacksonObjectMapper().writeValueAsString(CreateCompanyDto(name = name, employees = employees))
}
```

2. Lambda with Receiver
```kotlin
class CreateCompanyDtoBuilder(private val employees: MutableList<CreateEmployeeDto> = mutableListOf()) {
    var name: String = "Entenhausen AG"

    // init: CreateEmployeeDtoBuilder.() -> Unit bedeutet, dass das Lambda, das als Argument übergeben wird, den Typ CreateEmployeeDtoBuilder als Receiver hat. 
    // Innerhalb des Lambdas können alle Eigenschaften und Methoden von Tag direkt aufgerufen werden.
    fun employee(init: CreateEmployeeDtoBuilder.() -> Unit) {
        val builder = CreateEmployeeDtoBuilder()    // default Werte
        builder.init()                              // Überschreiben der Default-Werte
        employees.add(builder.build())
    }
    
    fun buildJson() = jacksonObjectMapper().writeValueAsString(CreateCompanyDto(name = name, employees = employees))
}
```

3. Vereinfachen des Lambdas with receiver
```kotlin
// init in sprechenderes block umbenannt
fun employee(block: CreateEmployeeDtoBuilder.() -> Unit) {
    employees.add(CreateEmployeeDtoBuilder().apply(block).build())
}
```

#### @DslMarker

* Autocompletion bietet mir alle Felder an
* Employee kann in Employee verschachtelt werden

```kotlin
@DslMarker
annotation class MyDsl

@MyDsl
class CreateEmployeeDtoBuilder

@MyDsl
class CreateCompanyDtoBuilder
```

Dann werden die Felder immer noch angeboten, aber der Compiler verhindert eine Nutzung

**Jetzt haben wir alles, was wir zum schreiben von DSLs benötigen**

#### WebTestFactory.kt

Was ich gerne hätte

```kotlin
apiTest(mockMvc) {
    post("/api/company/") {
        prepare {
            // mocking stuff
        }
        body {
            // body dsl stuff
        }
        verify {
            // status, content, ...
        }
    }
}
```

WebTestFactory.kt
```kotlin
@MyDsl
class PostBuilder(private val contextPath: String) {

    var preparation: (Any.() -> Unit)? = null
    var body: String = ""
    var validation: (MockMvcResultMatchersDsl.() -> Unit)? = null

    fun prepare(block: Any.() -> Unit) {
        this.preparation = block
    }

    fun body(block: CreateCompanyDtoBuilder.() -> Unit) {
        this.body = CreateCompanyDtoBuilder().apply(block).buildJson()
    }

    fun verify(dsl: MockMvcResultMatchersDsl.() -> Unit) {
        this.validation = dsl
    }
}

fun post(contextPath: String, block: PostBuilder.() -> Unit) {
    PostBuilder(contextPath).apply(block)
}
```

CompanyControllerTest.kt
```kotlin
@Test
fun `create a new company`() {
    post("/api/company/") {
        prepare {
            every { companyRepositoryMock.save(any()) } returnsArgument 0
        }
        body {
            name = "Panzerknacker AG"
            employee {
                name = "Karlchen Knack"
                email = "karlchen@knack.de"
            }
            employee {
                name = "Kuno Knack"
                email = "kuno@knack.de"
            }
        }
        verify {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.id") { exists() } }
            content { jsonPath("$.name") { value("Panzerknacker AG") } }
            content { jsonPath("$.employees[0].id") { exists() } }
            content { jsonPath("$.employees[0].name") { value("Karlchen Knack") } }
            content { jsonPath("$.employees[0].email") { value("karlchen@knack.de") } }
            content { jsonPath("$.employees[1].id") { exists() } }
            content { jsonPath("$.employees[1].name") { value("Kuno Knack") } }
            content { jsonPath("$.employees[1].email") { value("kuno@knack.de") } }
        }
    }
}
```

Es fehlt der Rahmen

```kotlin
class ApiTestBuilder(private val mockMvc: MockMvc) {

    private var postBuilder: PostBuilder? = null

    fun post(contextPath: String, block: PostBuilder.() -> Unit) {
        this.postBuilder = PostBuilder(contextPath).apply(block)
    }

    fun execute() {
        postBuilder?.let {
            it.preparation?.invoke {}

            val post = mockMvc.post(it.contextPath) {
                contentType = MediaType.APPLICATION_JSON
                content = it.body
            }

            if (it.validation != null) {
                post.andExpect(it.validation!!)
            }
        }
    }
}

fun apiTest(mockMvc: MockMvc, block: ApiTestBuilder.() -> Unit) = ApiTestBuilder(mockMvc).apply(block).execute()
```

Anpassung Test + Ausführen

```kotlin
@Test
fun `create a new company`() {
    apiTest(mockMvc) {
        post("/api/company/") {
            prepare {
                every { companyRepositoryMock.save(any()) } returnsArgument 0
            }
            body {
                name = "Panzerknacker AG"
                employee {
                    name = "Karlchen Knack"
                    email = "karlchen@knack.de"
                }
                employee {
                    name = "Kuno Knack"
                    email = "kuno@knack.de"
                }
            }
            verify {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { jsonPath("$.id") { exists() } }
                content { jsonPath("$.name") { value("Panzerknacker AG") } }
                content { jsonPath("$.employees[0].id") { exists() } }
                content { jsonPath("$.employees[0].name") { value("Karlchen Knack") } }
                content { jsonPath("$.employees[0].email") { value("karlchen@knack.de") } }
                content { jsonPath("$.employees[1].id") { exists() } }
                content { jsonPath("$.employees[1].name") { value("Kuno Knack") } }
                content { jsonPath("$.employees[1].email") { value("kuno@knack.de") } }
            }
        }
    }
}
```