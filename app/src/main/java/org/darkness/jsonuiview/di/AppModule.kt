package org.darkness.jsonuiview.di

import android.content.Context
import org.darkness.jsonuiview.data.local.db.AppDatabase
import org.darkness.jsonuiview.data.parser.BindingEvaluator
import org.darkness.jsonuiview.data.parser.FormExtractor
import org.darkness.jsonuiview.data.parser.JsonUiParser
import org.darkness.jsonuiview.data.parser.ModificationApplier
import org.darkness.jsonuiview.data.parser.ReferenceResolver
import org.darkness.jsonuiview.data.repository.FormRepository
import org.darkness.jsonuiview.util.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named

val AppModule = module {
    single<AppDatabase> { AppDatabase.getDatabase(androidContext()) }
    
    single<ReferenceResolver> { ReferenceResolver() }
    single<BindingEvaluator> { BindingEvaluator() }
    single<ModificationApplier> { ModificationApplier() }
    single<FormExtractor> { FormExtractor() }
    single<JsonUiParser> { JsonUiParser(get(), get(), get(), get()) }
    
    single<FormRepository> { FormRepository(get(), get()) }
    
    single<PreferencesManager> { PreferencesManager(androidContext()) }
}