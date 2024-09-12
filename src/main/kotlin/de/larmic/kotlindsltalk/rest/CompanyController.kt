package de.larmic.kotlindsltalk.rest

import de.larmic.kotlindsltalk.database.CompanyEntity
import de.larmic.kotlindsltalk.database.CompanyRepository
import de.larmic.kotlindsltalk.database.EmployeeEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("api/company")
class CompanyController(private val companyRepository: CompanyRepository) {

    @PostMapping("/", consumes = ["application/json"], produces = ["application/json"])
    fun create(@RequestBody dto: CreateCompanyDto) : ReadCompanyDto {
        val companyEntity = mapToEntity(dto)
        companyRepository.save(companyEntity)
        return mapToDto(companyEntity)
    }

    @GetMapping("/{id}")
    fun getCompany(@PathVariable id: UUID): ResponseEntity<ReadCompanyDto> {
        if (companyRepository exists id) {
            val companyEntity = companyRepository.get(id)
            return wrapInResponse(companyEntity)
        }

        return ResponseEntity.notFound().build()
    }
}

// dtos
class CreateEmployeeDto(val name: String, val email: String)
class ReadEmployeeDto(val id: UUID, val name: String, val email: String)

class CreateCompanyDto(val name: String, val employees: List<CreateEmployeeDto> = emptyList())
class ReadCompanyDto(val id: UUID, val name: String, val employees: List<ReadEmployeeDto> = emptyList())

// mapping dto to entity
private fun mapToEntity(dto: CreateEmployeeDto) = EmployeeEntity(name = dto.name, email = dto.email)
private fun mapToEntity(dto: CreateCompanyDto) = CompanyEntity(name = dto.name, employees = dto.employees.map { mapToEntity(it) }.toMutableList())

// mapping entity to dto
private fun mapToDto(entity: CompanyEntity) = ReadCompanyDto(id = entity.id, name = entity.name, employees = entity.employees.map { mapToDto(it) })
private fun mapToDto(entity: EmployeeEntity) = ReadEmployeeDto(id = entity.id, name = entity.name, email = entity.email)

// mapping other stuff
private fun wrapInResponse(entity: CompanyEntity) = ResponseEntity.ok(mapToDto(entity))
