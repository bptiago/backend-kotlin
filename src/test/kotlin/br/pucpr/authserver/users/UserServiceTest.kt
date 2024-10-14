package br.pucpr.authserver.users

import br.pucpr.authserver.errors.BadRequestException
import io.kotest.matchers.shouldBe
import io.mockk.checkUnnecessaryStub
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserServiceTest {
    private val repositoryMock = mockk<UserRepository>()
    private val service = UserService(
        repository = repositoryMock
    )

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub()
    }

    @Test
    fun `insert throws BadRequestException if the email exists`() {
        val user = user(email="user@email.com")
        every { repositoryMock.findByEmailOrNull(user.email) } returns user

        assertThrows<BadRequestException> {
            service.insert(user)
        }
    }

    @Test
    fun `insert saves the data if the email does not exists`() {
        val user = user(id=null)
        val new_user = user(id=1)
        every { repositoryMock.findByEmailOrNull(user.email) } returns null
        every { repositoryMock.save(user) } returns new_user
        service.insert(user) shouldBe new_user
    }

    @Test
    fun `insert throws IllegalArgumentException if the user has an id`() {
        val user_with_id = user(id=1)
        assertThrows<IllegalArgumentException> {
            service.insert(user_with_id)
        }
    }
}
