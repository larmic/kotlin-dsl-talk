package de.larmic.kotlindsltalk.database

import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
class CompanyRepository {

    private val companies = mutableMapOf<UUID, CompanyEntity>()

    fun save(company: CompanyEntity): CompanyEntity {
        companies.put(company.id, company)
        return company
    }

    fun get(id: UUID) = companies[id]!!

    infix fun exists(id: UUID) = companies.get(id) != null
}

class CompanyEntity(
    val id: UUID = UUID.randomUUID(),
    val createDate: LocalDateTime = LocalDateTime.now(),
    var lastUpdateDate: LocalDateTime = LocalDateTime.now(),
    var name: String,
    val employees: MutableList<EmployeeEntity> = mutableListOf(),
)

class EmployeeEntity(
    val id: UUID = UUID.randomUUID(),
    val createDate: LocalDateTime = LocalDateTime.now(),
    var lastUpdateDate: LocalDateTime = LocalDateTime.now(),
    var name: String,
    var email: String,
)