package com.ecommerce.commonlib.autoconfigure;

import com.ecommerce.commonlib.i18n.Messages;
import com.ecommerce.commonlib.web.i18n.I18nProperties;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;

/**
 * Sets up the platform-wide {@link MessageSource} and bridges it into the static
 * {@link Messages} helper so non-Spring contexts (exception constructors, etc.) can
 * resolve i18n keys with the same locale resolution rules as MVC handlers.
 *
 * <p>Also publishes an {@link AcceptHeaderLocaleResolver} so the locale is picked from the
 * incoming {@code Accept-Language} header instead of {@code Locale.getDefault()}.</p>
 *
 * <h3>Why SmartInitializingSingleton instead of @PostConstruct?</h3>
 * {@code @PostConstruct} runs while the owning bean is still being constructed — if
 * {@code MessageSource} is defined inside this same class, Spring hasn't finished wiring it
 * yet, producing a circular-dependency error. {@link SmartInitializingSingleton} is invoked
 * by Spring after <em>all</em> singleton beans are fully initialized, so the
 * {@code MessageSource} bean (ours or a custom one) is always present by then.
 */
@AutoConfiguration
@EnableConfigurationProperties(I18nProperties.class)
public class I18nAutoConfiguration {

    private final I18nProperties props;

    public I18nAutoConfiguration(I18nProperties props) {
        this.props = props;
    }

    @Bean
    @ConditionalOnMissingBean(MessageSource.class)
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        List<String> basenames = props.basenames();
        source.setBasenames(basenames.toArray(new String[0]));
        source.setDefaultEncoding(props.encoding().name());
        source.setDefaultLocale(props.defaultLocale());
        source.setFallbackToSystemLocale(props.fallbackToSystemLocale());
        source.setUseCodeAsDefaultMessage(props.useCodeAsDefaultMessage());
        source.setCacheMillis(props.cacheDuration().toMillis());
        return source;
    }

    @Bean
    @ConditionalOnMissingBean(LocaleResolver.class)
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(props.defaultLocale());
        resolver.setSupportedLocales(List.of(props.defaultLocale(), java.util.Locale.ENGLISH));
        return resolver;
    }

    /**
     * Wires the static {@link Messages} facade after all singletons are ready.
     * Taking {@code MessageSource} as a parameter lets Spring inject whichever bean
     * exists in the context — ours or a custom override — with no circular dependency.
     */
    @Bean
    public SmartInitializingSingleton i18nStaticBridge(MessageSource messageSource) {
        return () -> Messages.setMessageSource(messageSource);
    }
}
