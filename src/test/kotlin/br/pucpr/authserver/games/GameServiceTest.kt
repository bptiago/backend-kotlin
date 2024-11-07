package br.pucpr.authserver.games

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.games.requests.UpdateGameRequest
import br.pucpr.authserver.studios.Studio
import br.pucpr.authserver.studios.StudioRepository
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
import java.time.LocalDate

class GameServiceTest {
    // Test data
    private companion object {
        const val TEST_ID = 1L
        const val TEST_STUDIO_ID = 1L
        const val TEST_NAME = "Test Game"
        const val TEST_OVERVIEW = "Test Overview"
        val TEST_LAUNCH_DATE = LocalDate.of(2024, 1, 1)
    }

    // System under test
    private lateinit var gameService: GameService

    // Mocks
    private val gameRepository = mockk<GameRepository>()
    private val studioRepository = mockk<StudioRepository>()

    @BeforeEach
    fun setup() {
        gameService = GameService(gameRepository, studioRepository)
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

    private fun createTestStudio(
        id: Long = TEST_STUDIO_ID,
        name: String = "Test Studio",
        location: String = "Test Location",
        creationDate: LocalDate = LocalDate.now(),
        games: MutableList<Game> = mutableListOf()
    ) = Studio(
        id = id,
        name = name,
        location = location,
        creationDate = creationDate,
        games = games
    )

    @Test
    fun `insert should throw IllegalArgumentException when game already has an ID`() {
        val gameWithId = createTestGame(id = TEST_ID)

        assertThrows<IllegalArgumentException> {
            gameService.insert(gameWithId, TEST_STUDIO_ID)
        }.message shouldBe "Registro com ID $TEST_ID já inserido!"
    }

    @Test
    fun `insert should throw BadRequestException when studio not found`() {
        val game = createTestGame()
        every { studioRepository.findByIdOrNull(TEST_STUDIO_ID) } returns null

        assertThrows<BadRequestException> {
            gameService.insert(game, TEST_STUDIO_ID)
        }.message shouldBe "Não há studio cadastrado com ID $TEST_STUDIO_ID"

        verify { studioRepository.findByIdOrNull(TEST_STUDIO_ID) }
    }

    @Test
    fun `insert should save and return game when successful`() {
        val game = createTestGame()
        val studio = createTestStudio()
        val savedStudio = createTestStudio().apply { games.add(game) }

        every { studioRepository.findByIdOrNull(TEST_STUDIO_ID) } returns studio
        every { studioRepository.save(studio) } returns savedStudio

        val result = gameService.insert(game, TEST_STUDIO_ID)

        result shouldBe game
        verify {
            studioRepository.findByIdOrNull(TEST_STUDIO_ID)
            studioRepository.save(studio)
        }
    }

    @Test
    fun `list should return all games sorted ascending`() {
        val games = listOf(
            createTestGame(1L, "Game A"),
            createTestGame(2L, "Game B")
        )
        every { gameRepository.findAll() } returns games

        val result = gameService.list(SortDir.ASC)

        result shouldBe games
        verify { gameRepository.findAll() }
    }

    @Test
    fun `list should return all games sorted descending`() {
        val games = listOf(
            createTestGame(2L, "Game B"),
            createTestGame(1L, "Game A")
        )
        every { gameRepository.findAll(Sort.by("id").reverse()) } returns games

        val result = gameService.list(SortDir.DESC)

        result shouldBe games
        verify { gameRepository.findAll(Sort.by("id").reverse()) }
    }

    @Test
    fun `list should return empty list when no games exist`() {
        every { gameRepository.findAll() } returns emptyList()

        val result = gameService.list(SortDir.ASC)

        result shouldBe emptyList()
        verify { gameRepository.findAll() }
    }

    @Test
    fun `getStudio should throw NotFoundException when game not found`() {
        every { gameRepository.findByIdOrNull(TEST_ID) } returns null

        assertThrows<NotFoundException> {
            gameService.getStudio(TEST_ID)
        }.message shouldBe "Não foi encontrado game com ID $TEST_ID"

        verify { gameRepository.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `getStudio should return game when found`() {
        val game = createTestGame(TEST_ID)
        every { gameRepository.findByIdOrNull(TEST_ID) } returns game

        val result = gameService.getStudio(TEST_ID)

        result shouldBe game
        verify { gameRepository.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `update should throw NotFoundException when game not found`() {
        val updateRequest = UpdateGameRequest(
            name = "Updated Name",
            overview = "Updated Overview",
            launchDate = LocalDate.now()
        )
        every { gameRepository.findByIdOrNull(TEST_ID) } returns null

        assertThrows<NotFoundException> {
            gameService.update(TEST_ID, updateRequest)
        }.message shouldBe "Não foi encontrado game com ID $TEST_ID"

        verify { gameRepository.findByIdOrNull(TEST_ID) }
    }


    @Test
    fun `delete should throw NotFoundException when game not found`() {
        every { gameRepository.findByIdOrNull(TEST_ID) } returns null

        assertThrows<NotFoundException> {
            gameService.delete(TEST_ID)
        }.message shouldBe "Não foi encontrado game com ID $TEST_ID"

        verify { gameRepository.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `delete should delete game when found`() {
        val game = createTestGame(TEST_ID)
        every { gameRepository.findByIdOrNull(TEST_ID) } returns game
        every { gameRepository.delete(game) } returns Unit

        gameService.delete(TEST_ID)

        verify {
            gameRepository.findByIdOrNull(TEST_ID)
            gameRepository.delete(game)
        }
    }
}