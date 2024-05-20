package com.setembreiros.artis.domain.usecase

import com.setembreiros.artis.data.repository.LocalPropertiesRepository
import java.util.Properties
import javax.inject.Inject

class GetPropertiesUseCase @Inject constructor(private val localPropertiesRepository: LocalPropertiesRepository) {
    fun invoke(): Properties{
        localPropertiesRepository.getProperties()?.let {
            return it
        }
        return error("Non se atopa o ficheiro de configuración secret.properties. Este ficheiro en imprescindíbel para a configuración das chaves de Cognito")
    }
}