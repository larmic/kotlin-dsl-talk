package de.larmic.kotlindsltalk.rest

import com.ninjasquad.springmockk.MockkBean
import de.larmic.kotlindsltalk.database.CompanyRepository
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(CompanyController::class)
class CompanyControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var companyRepositoryMock: CompanyRepository

    @Test
    fun `create a new company`() {
        every { companyRepositoryMock.save(any()) } returnsArgument 0

        this.mockMvc.post("/api/company/") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "name": "Panzerknacker AG",
                    "employees": [
                        {
                            "name": "Karlchen Knack",
                            "email": "karlchen@knack.de"
                        },
                        {
                            "name": "Kuno Knack",
                            "email": "kuno@knack.de"
                        }   
                    ]
                }
            """.trimIndent()
        }.andExpect {
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