package br.pucpr.authserver.users

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.security.Jwt
import br.pucpr.authserver.users.responses.LoginResponse
import br.pucpr.authserver.users.responses.UserResponse
import br.pucpr.authserver.utils.SortDir
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull

class UserServiceTest {
    // Test data
    private companion object {
        const val TEST_NAME = "John Doe"
        const val TEST_PASSWORD = "password123"
        const val TEST_EMAIL = "john.doe@email.com"
        const val TEST_ID = 1L
    }

    // System under test
    private lateinit var userService: UserService

    // Mocks
    private val userRepository = mockk<UserRepository>()
    private val roleRepository = mockk<RoleRepository>()
    private val jwt = mockk<Jwt>()

    @BeforeEach
    fun setup() {
        userService = UserService(userRepository, roleRepository, jwt)
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    private fun createTestUser(id: Long? = null) = User(
        id = id,
        name = TEST_NAME,
        password = TEST_PASSWORD,
        email = TEST_EMAIL
    )


    @Test
    fun `insert should throw IllegalArgumentException when user already has an ID`() {
        val userWithId = createTestUser(id = 1L)
        assertThrows<IllegalArgumentException> {
            userService.insert(userWithId)
        }
    }

    @Test
    fun `insert should throw BadRequestException when email already exists`() {
        val user = createTestUser()
        every { userRepository.findByEmail(TEST_EMAIL) } returns createTestUser(id = 1L)

        assertThrows<BadRequestException> {
            userService.insert(user)
        }.message shouldBe "Usuário com email john.doe@email.com já existe!"

        verify { userRepository.findByEmail(TEST_EMAIL) }
    }

    @Test
    fun `insert should save and return user when email does not exist`() {
        val user = createTestUser()
        val savedUser = createTestUser(id = 1L)

        every { userRepository.findByEmail(TEST_EMAIL) } returns null
        every { userRepository.save(user) } returns savedUser

        val result = userService.insert(user)

        result shouldBe savedUser
        verify {
            userRepository.findByEmail(TEST_EMAIL)
            userRepository.save(user)
        }
    }

    @Test
    fun `list should return all users sorted ascending when no role is specified`() {
        val users = listOf(createTestUser(1L), createTestUser(2L))
        every { userRepository.findAll() } returns users

        val result = userService.list(SortDir.ASC, null)

        result shouldBe users
        verify { userRepository.findAll() }
    }

    @Test
    fun `list should return all users sorted descending when no role is specified`() {
        val users = listOf(createTestUser(2L), createTestUser(1L))
        every { userRepository.findAll(Sort.by("id").reverse()) } returns users

        val result = userService.list(SortDir.DESC, null)

        result shouldBe users
        verify { userRepository.findAll(Sort.by("id").reverse()) }
    }

    @Test
    fun `list should return users filtered by role when role is specified`() {
        val users = listOf(createTestUser(1L), createTestUser(2L))
        every { userRepository.findByRole("ADMIN") } returns users

        val result = userService.list(SortDir.ASC, "ADMIN")

        result shouldBe users
        verify { userRepository.findByRole("ADMIN") }
    }

    @Test
    fun `findByIdOrNull should return user when found`() {
        val user = createTestUser(TEST_ID)
        every { userRepository.findByIdOrNull(TEST_ID) } returns user

        val result = userService.findByIdOrNull(TEST_ID)

        result shouldBe user
        verify { userRepository.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `findByIdOrNull should return null when user not found`() {
        every { userRepository.findByIdOrNull(TEST_ID) } returns null

        val result = userService.findByIdOrNull(TEST_ID)

        result shouldBe null
        verify { userRepository.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `findByEmailOrNull should return user when found`() {
        val user = createTestUser(TEST_ID)
        every { userRepository.findByEmail(TEST_EMAIL) } returns user

        val result = userService.findByEmailOrNull(TEST_EMAIL)

        result shouldBe user
        verify { userRepository.findByEmail(TEST_EMAIL) }
    }

    @Test
    fun `delete should return null when user not found`() {
        every { userRepository.findByIdOrNull(TEST_ID) } returns null

        val result = userService.delete(TEST_ID)

        result shouldBe null
        verify { userRepository.findByIdOrNull(TEST_ID) }
    }



    @Test
    fun `delete should delete and return user when successful`() {
        val user = createTestUser(TEST_ID)
        every { userRepository.findByIdOrNull(TEST_ID) } returns user
        every { userRepository.delete(user) } returns Unit

        val result = userService.delete(TEST_ID)

        result shouldBe user
        verify {
            userRepository.findByIdOrNull(TEST_ID)
            userRepository.delete(user)
        }
    }

    @Test
    fun `update should throw NotFoundException when user not found`() {
        every { userRepository.findByIdOrNull(TEST_ID) } returns null

        assertThrows<NotFoundException> {
            userService.update(TEST_ID, "New Name")
        }.message shouldBe "Usuário $TEST_ID não encontrado!"

        verify { userRepository.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `update should return null when name hasn't changed`() {
        val user = createTestUser(TEST_ID)
        every { userRepository.findByIdOrNull(TEST_ID) } returns user

        val result = userService.update(TEST_ID, TEST_NAME)

        result shouldBe null
        verify { userRepository.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `addRole should throw NotFoundException when user not found`() {
        every { userRepository.findByIdOrNull(TEST_ID) } returns null

        assertThrows<NotFoundException> {
            userService.addRole(TEST_ID, "USER")
        }.message shouldBe "Usuário não encontrado"

        verify { userRepository.findByIdOrNull(TEST_ID) }
    }


    @Test
    fun `addRole should throw BadRequestException when role doesn't exist`() {
        val user = createTestUser(TEST_ID)
        every { userRepository.findByIdOrNull(TEST_ID) } returns user
        every { roleRepository.findByName("INVALID_ROLE") } returns null

        assertThrows<BadRequestException> {
            userService.addRole(TEST_ID, "INVALID_ROLE")
        }.message shouldBe "Invalid role name!"

        verify {
            userRepository.findByIdOrNull(TEST_ID)
            roleRepository.findByName("INVALID_ROLE")
        }
    }



    @Test
    fun `login should return null when user not found`() {
        every { userRepository.findByEmail(TEST_EMAIL) } returns null

        val result = userService.login(TEST_EMAIL, TEST_PASSWORD)

        result shouldBe null
        verify { userRepository.findByEmail(TEST_EMAIL) }
    }

    @Test
    fun `login should return null when password is incorrect`() {
        val user = createTestUser(TEST_ID)
        every { userRepository.findByEmail(TEST_EMAIL) } returns user

        val result = userService.login(TEST_EMAIL, "wrong_password")

        result shouldBe null
        verify { userRepository.findByEmail(TEST_EMAIL) }
    }

    @Test
    fun `login should return LoginResponse when credentials are correct`() {
        val user = createTestUser(TEST_ID)
        val token = "jwt_token"
        val expectedResponse = LoginResponse(token, UserResponse(user))

        every { userRepository.findByEmail(TEST_EMAIL) } returns user
        every { jwt.createToken(user) } returns token

        val result = userService.login(TEST_EMAIL, TEST_PASSWORD)

        result shouldBe expectedResponse
        verify {
            userRepository.findByEmail(TEST_EMAIL)
            jwt.createToken(user)
        }
    }
}