import com.megboyzz.devmenu.domain.repository.FilesRepository
import com.megboyzz.devmenu.domain.repository.ReplacementRepository
import com.megboyzz.devmenu.domain.repository.SaveRepository
import com.megboyzz.devmenu.domain.repository.SettingsRepository
import com.megboyzz.devmenu.domain.repository.SvmwRepository
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.File
import kotlin.coroutines.coroutineContext

class UseCaseTest {

    private val filesRepository = mock(FilesRepository::class.java)
    private val replacementRepository = mock(ReplacementRepository::class.java)
    private val saveRepository = mock(SaveRepository::class.java)
    private val settingsRepository = mock(SettingsRepository::class.java)
    private val svmwRepository = mock(SvmwRepository::class.java)

    private val externalFile = File("D:\\NFSMW Online\\com.ea.games.nfs13_na")
    private val internalFile = File("D:\\NFSMW Online")

    init {

        `when`(filesRepository.externalRoot).thenReturn(externalFile)
        `when`(filesRepository.internalRoot).thenReturn(internalFile)


        //`when`(replacementRepository.getAllReplacements()).thenReturn()


    }

}