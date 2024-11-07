package br.pucpr.authserver.studios

import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.games.Game
import br.pucpr.authserver.studios.requests.UpdateStudioRequest
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

class StudioServiceTest {
    // Test data
    private companion object {
        const val TEST_ID = 1L
        const val TEST_NAME = "Test Studio"
        const val TEST_LOCATION = "Test Location"
        val TEST_DATE = LocalDate.of(2024, 1, 1)
    }

    // System under test
    private lateinit var studioService: StudioService

    // Mocks
    private val studioRepository = mockk<StudioRepository>()

    @BeforeEach
    fun setup() {
        studioService = StudioService(studioRepository)
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    private fun createTestGame(id: Long = 1L) = Game(
        id = id,
        name = "Test Game $id",
        overview = "Test Overview",
        launchDate = LocalDate.now()
    )

    private fun createTestStudio(
        id: Long? = null,
        name: String = TEST_NAME,
        location: String = TEST_LOCATION,
        creationDate: LocalDate = TEST_DATE,
        games: MutableList<Game> = mutableListOf(createTestGame())
    ) = Studio(
        id = id,
        name = name,
        location = location,
        creationDate = creationDate,
        games = games
    )

    @Test
    fun `insert should throw IllegalArgumentException when studio already has an ID`() {
        val studioWithId = createTestStudio(id = TEST_ID)

        assertThrows<IllegalArgumentException> {
            studioService.insert(studioWithId)
        }.message shouldBe "Registro com ID $TEST_ID já inserido"
    }

    @Test
    fun `insert should save and return studio when ID is null`() {
        val studio = createTestStudio()
        val savedStudio = createTestStudio(id = TEST_ID)

        every { studioRepository.save(studio) } returns savedStudio

        val result = studioService.insert(studio)

        result shouldBe savedStudio
        verify { studioRepository.save(studio) }
    }

    @Test
    fun `getStudio should throw NotFoundException when studio not found`() {
        every { studioRepository.findByIdOrNull(TEST_ID) } returns null

        assertThrows<NotFoundException> {
            studioService.getStudio(TEST_ID)
        }.message shouldBe "Não foi encontrado studio com ID $TEST_ID"

        verify { studioRepository.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `getStudio should return studio when found`() {
        val studio = createTestStudio(TEST_ID)
        every { studioRepository.findByIdOrNull(TEST_ID) } returns studio

        val result = studioService.getStudio(TEST_ID)

        result shouldBe studio
        verify { studioRepository.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `list should return all studios sorted ascending`() {
        val studios = listOf(
            createTestStudio(1L, "Studio A"),
            createTestStudio(2L, "Studio B")
        )
        every { studioRepository.findAll() } returns studios

        val result = studioService.list(SortDir.ASC)

        result shouldBe studios
        verify { studioRepository.findAll() }
    }

    @Test
    fun `list should return all studios sorted descending`() {
        val studios = listOf(
            createTestStudio(2L, "Studio B"),
            createTestStudio(1L, "Studio A")
        )
        every { studioRepository.findAll(Sort.by("id").reverse()) } returns studios

        val result = studioService.list(SortDir.DESC)

        result shouldBe studios
        verify { studioRepository.findAll(Sort.by("id").reverse()) }
    }

    @Test
    fun `list should return empty list when no studios exist`() {
        every { studioRepository.findAll() } returns emptyList()

        val result = studioService.list(SortDir.ASC)

        result shouldBe emptyList()
        verify { studioRepository.findAll() }
    }

    @Test
    fun `update should throw NotFoundException when studio not found`() {
        val updateRequest = UpdateStudioRequest(
            name = "Updated Name",
            location = "Updated Location",
            creationDate = LocalDate.now()
        )
        every { studioRepository.findByIdOrNull(TEST_ID) } returns null

        assertThrows<NotFoundException> {
            studioService.update(TEST_ID, updateRequest)
        }.message shouldBe "Não foi encontrado studio com ID $TEST_ID"

        verify { studioRepository.findByIdOrNull(TEST_ID) }
    }



    @Test
    fun `delete should throw NotFoundException when studio not found`() {
        every { studioRepository.findByIdOrNull(TEST_ID) } returns null

        assertThrows<NotFoundException> {
            studioService.delete(TEST_ID)
        }.message shouldBe "Não foi encontrado studio com ID $TEST_ID"

        verify { studioRepository.findByIdOrNull(TEST_ID) }
    }

    @Test
    fun `delete should delete studio when found`() {
        val studio = createTestStudio(TEST_ID)
        every { studioRepository.findByIdOrNull(TEST_ID) } returns studio
        every { studioRepository.delete(studio) } returns Unit

        studioService.delete(TEST_ID)

        verify {
            studioRepository.findByIdOrNull(TEST_ID)
            studioRepository.delete(studio)
        }
    }
}