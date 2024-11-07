package br.pucpr.authserver.games

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.games.requests.CreateGameRequest
import br.pucpr.authserver.games.requests.UpdateGameRequest
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
import java.time.LocalDate

class GameControllerTest {
    // Test data
    private companion object {
        const val TEST_ID = 1L
        const val TEST_STUDIO_ID = 1L
        const val TEST_NAME = "Test Game"
        const val TEST_OVERVIEW = "Test Overview"
        val TEST_LAUNCH_DATE = LocalDate.of(2024, 1, 1)
    }

    // System under test
    private lateinit var gameController: GameController

    // Mocks
    private val gameService = mockk<GameService>()

    @BeforeEach
    fun setup() {
        gameController = GameController(gameService)
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    private fun createTestGame(
        id: Long? = null,
        name: String = TEST_NAME,
        overview: String = TEST_OVERVIEW,
        launchDate: LocalDate = TEST_LAUNCH_DATE
    ) = Game(
        id = id,
        name = name,
        overview = overview,
        launchDate = launchDate
    )

    private fun createTestGameRequest(
        name: String = TEST_NAME,
        overview: String = TEST_OVERVIEW,
        launchDate: LocalDate = TEST_LAUNCH_DATE,
        fkStudioId: Long = TEST_STUDIO_ID
    ) = CreateGameRequest(
        name = name,
        overview = overview,
        launchDate = launchDate,
        fkStudioId = fkStudioId
    )

    private fun createTestUpdateRequest(
        name: String = TEST_NAME,
        overview: String = TEST_OVERVIEW,
        launchDate: LocalDate = TEST_LAUNCH_DATE
    ) = UpdateGameRequest(
        name = name,
        overview = overview,
        launchDate = launchDate
    )

    @Test
    fun `insert should return CREATED status and game when successful`() {
        val request = createTestGameRequest()
        val game = createTestGame()

        every { gameService.insert(any(), TEST_STUDIO_ID) } returns game

        val response = gameController.insert(request)

        response.statusCode shouldBe HttpStatus.CREATED
        response.body shouldBe game

        verify { gameService.insert(any(), TEST_STUDIO_ID) }
    }

    @Test
    fun `list should return OK status and games list when no sort direction specified`() {
        val games = listOf(createTestGame(1L), createTestGame(2L))
        every { gameService.list(SortDir.ASC) } returns games

        val response = gameController.list(null)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe games

        verify { gameService.list(SortDir.ASC) }
    }

    @Test
    fun `list should return OK status and games list with specified sort direction`() {
        val games = listOf(createTestGame(1L), createTestGame(2L))
        every { gameService.list(SortDir.DESC) } returns games

        val response = gameController.list("DESC")

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe games

        verify { gameService.list(SortDir.DESC) }
    }

    @Test
    fun `list should throw BadRequestException when invalid sort direction provided`() {
        assertThrows<BadRequestException> {
            gameController.list("INVALID")
        }.message shouldBe "Parâmetro SORT inválido (deve ser ASC ou DESC)"
    }

    @Test
    fun `findById should return OK status and game when found`() {
        val game = createTestGame(TEST_ID)
        every { gameService.getStudio(TEST_ID) } returns game

        val response = gameController.findById(TEST_ID)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe game

        verify { gameService.getStudio(TEST_ID) }
    }

    @Test
    fun `update should return OK status and updated game`() {
        val updateRequest = createTestUpdateRequest()
        val updatedGame = createTestGame(TEST_ID)

        every { gameService.update(TEST_ID, updateRequest) } returns updatedGame

        val response = gameController.update(TEST_ID, updateRequest)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe updatedGame

        verify { gameService.update(TEST_ID, updateRequest) }
    }

    @Test
    fun `delete should return OK status when successful`() {
        every { gameService.delete(TEST_ID) } returns Unit

        val response = gameController.delete(TEST_ID)

        response.statusCode shouldBe HttpStatus.OK

        verify { gameService.delete(TEST_ID) }
    }
}