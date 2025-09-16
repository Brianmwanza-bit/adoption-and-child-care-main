import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.adoptionapp.ChildrenEntity
import com.adoptionapp.repository.ChildRepository
import com.adoptionapp.viewmodel.ChildViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class ChildViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var repository: ChildRepository
    private lateinit var viewModel: ChildViewModel
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        repository = mock(ChildRepository::class.java)
        viewModel = ChildViewModel(repository)
    }

    @Test
    fun testLoadingStateDuringAddChild() = testScope.runTest {
        val loadingStates = mutableListOf<Boolean>()
        val observer = Observer<Boolean> { loadingStates.add(it) }
        viewModel.loading.asLiveData().observeForever(observer)
        val child = mock(ChildrenEntity::class.java)
        viewModel.addChild(child)
        // Should see true (loading) then false (done)
        assertTrue(loadingStates.contains(true))
        assertTrue(loadingStates.contains(false))
    }

    @Test
    fun testErrorStateOnException() = testScope.runTest {
        val errorStates = mutableListOf<String?>()
        val observer = Observer<String?> { errorStates.add(it) }
        viewModel.error.asLiveData().observeForever(observer)
        val child = mock(ChildrenEntity::class.java)
        `when`(repository.insert(child)).thenThrow(RuntimeException("Test error"))
        viewModel.addChild(child)
        assertTrue(errorStates.any { it?.contains("Test error") == true })
    }
} 