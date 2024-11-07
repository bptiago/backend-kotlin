package br.pucpr.authserver.users

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.errors.ForbiddenException
import br.pucpr.authserver.security.UserToken
import br.pucpr.authserver.users.requests.CreateUserRequest
import br.pucpr.authserver.users.requests.LoginRequest
import br.pucpr.authserver.users.requests.UpdateUserRequest
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
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication

class UserControllerTest {
    // Test data
    private companion object {
        const val TEST_ID = 1L
        const val TEST_NAME = "John Doe"
        const val TEST_EMAIL = "john.doe@email.com"
        const val TEST_PASSWORD = "password123"
    }

    // System under test
    private lateinit var userController: UserController

    // Mocks
    private val userService = mockk<UserService>()
    private val authentication = mockk<Authentication>()

    @BeforeEach
    fun setup() {
        userController = UserController(userService)
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    private fun createTestUser(id: Long? = null) = User(
        id = id,
        name = TEST_NAME,
        email = TEST_EMAIL,
        password = TEST_PASSWORD
    )

    @Test
    fun `insert should return created status with user response`() {
        val createRequest = CreateUserRequest(TEST_NAME, TEST_EMAIL, TEST_PASSWORD)
        val user = createTestUser(TEST_ID)

        every { userService.insert(any()) } returns user

        val response = userController.insert(createRequest)

        response.statusCode shouldBe HttpStatus.CREATED
        response.body shouldBe UserResponse(user)
        verify { userService.insert(any()) }
    }

    @Test
    fun `list should return ok status with user responses`() {
        val users = listOf(createTestUser(1L), createTestUser(2L))
        every { userService.list(SortDir.ASC, null) } returns users

        val response = userController.list("ASC", null)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe users.map { UserResponse(it) }
        verify { userService.list(SortDir.ASC, null) }
    }

    @Test
    fun `list should throw BadRequestException for invalid sort direction`() {
        assertThrows<BadRequestException> {
            userController.list("INVALID", null)
        }.message shouldBe "Invalid sort dir!"
    }

    @Test
    fun `findById should return ok status with user response when found`() {
        val user = createTestUser(TEST_ID)
        every { userService.findByIdOrNull(TEST_ID) } returns user

        val response = userController.findById(TEST_ID)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe UserResponse(user)
        verify { userService.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `findById should return not found when user doesn't exist`() {
        every { userService.findByIdOrNull(TEST_ID) } returns null

        val response = userController.findById(TEST_ID)

        response.statusCode shouldBe HttpStatus.NOT_FOUND
        verify { userService.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `delete should return ok when successful`() {
        val user = createTestUser(TEST_ID)
        every { userService.delete(TEST_ID) } returns user

        val response = userController.delete(TEST_ID)

        response.statusCode shouldBe HttpStatus.OK
        verify { userService.delete(TEST_ID) }
    }

    @Test
    fun `delete should return not found when user doesn't exist`() {
        every { userService.delete(TEST_ID) } returns null

        val response = userController.delete(TEST_ID)

        response.statusCode shouldBe HttpStatus.NOT_FOUND
        verify { userService.delete(TEST_ID) }
    }

    @Test
    fun `update should return ok with updated user when successful`() {
        val user = createTestUser(TEST_ID)
        val updateRequest = UpdateUserRequest("New Name")
        val userToken = UserToken(TEST_ID, TEST_EMAIL, setOf("USER"))

        every { authentication.principal } returns userToken
        every { userService.update(TEST_ID, "New Name") } returns user

        val response = userController.update(TEST_ID, updateRequest, authentication)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe UserResponse(user)
        verify {
            authentication.principal
            userService.update(TEST_ID, "New Name")
        }
    }

    @Test
    fun `update should return no content when no changes made`() {
        val updateRequest = UpdateUserRequest("New Name")
        val userToken = UserToken(TEST_ID, TEST_EMAIL, setOf("USER"))

        every { authentication.principal } returns userToken
        every { userService.update(TEST_ID, "New Name") } returns null

        val response = userController.update(TEST_ID, updateRequest, authentication)

        response.statusCode shouldBe HttpStatus.NO_CONTENT
        verify {
            authentication.principal
            userService.update(TEST_ID, "New Name")
        }
    }

    @Test
    fun `update should throw ForbiddenException when user token is invalid`() {
        val updateRequest = UpdateUserRequest("New Name")
        every { authentication.principal } returns null

        assertThrows<ForbiddenException> {
            userController.update(TEST_ID, updateRequest, authentication)
        }

        verify { authentication.principal }
    }

    @Test
    fun `update should throw ForbiddenException when non-admin user tries to update another user`() {
        val updateRequest = UpdateUserRequest("New Name")
        val userToken = UserToken(2L, TEST_EMAIL, setOf("USER"))

        every { authentication.principal } returns userToken

        assertThrows<ForbiddenException> {
            userController.update(TEST_ID, updateRequest, authentication)
        }

        verify { authentication.principal }
    }

    @Test
    fun `grant should return ok when role added successfully`() {
        every { userService.addRole(TEST_ID, "ADMIN") } returns true

        val response = userController.grant(TEST_ID, "ADMIN")

        response.statusCode shouldBe HttpStatus.OK
        verify { userService.addRole(TEST_ID, "ADMIN") }
    }

    @Test
    fun `grant should return no content when role already exists`() {
        every { userService.addRole(TEST_ID, "ADMIN") } returns false

        val response = userController.grant(TEST_ID, "ADMIN")

        response.statusCode shouldBe HttpStatus.NO_CONTENT
        verify { userService.addRole(TEST_ID, "ADMIN") }
    }

    @Test
    fun `login should return ok with login response when successful`() {
        val loginRequest = LoginRequest(TEST_EMAIL, TEST_PASSWORD)
        val loginResponse = LoginResponse("token", UserResponse(createTestUser(TEST_ID)))

        every { userService.login(TEST_EMAIL, TEST_PASSWORD) } returns loginResponse

        val response = userController.login(loginRequest)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe loginResponse
        verify { userService.login(TEST_EMAIL, TEST_PASSWORD) }
    }

    @Test
    fun `login should return unauthorized when credentials are invalid`() {
        val loginRequest = LoginRequest(TEST_EMAIL, TEST_PASSWORD)

        every { userService.login(TEST_EMAIL, TEST_PASSWORD) } returns null

        val response = userController.login(loginRequest)

        response.statusCode shouldBe HttpStatus.UNAUTHORIZED
        verify { userService.login(TEST_EMAIL, TEST_PASSWORD) }
    }
}