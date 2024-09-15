package de.larmic.kotlindsltalk.database

import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class CompanyRepository {

    private val companies = mutableMapOf<UUID, CompanyEntity>()

    fun save(company: CompanyEntity): CompanyEntity {
        companies[company.id] = company
        return company
    }

    fun get(id: UUID) = companies[id]!!

    fun exists(id: UUID) = companies[id] != null
}

class CompanyEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    val employees: MutableList<EmployeeEntity> = mutableListOf(),
)

class EmployeeEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var email: String,
)