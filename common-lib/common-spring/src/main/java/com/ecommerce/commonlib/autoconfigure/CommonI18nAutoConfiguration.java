package com.ecommerce.commonlib.autoconfigure;

import com.ecommerce.commonlib.i18n.Messages;
import com.ecommerce.commonlib.web.i18n.I18nProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import jakarta.annotation.PostConstruct;

import java.util.List;

/**
 * Sets up the platform-wide {@link MessageSource} and bridges it into the static
 * {@link Messages} helper so non-Spring contexts (exception constructors, etc.) can
 * resolve i18n keys with the same locale resolution rules as MVC handlers.
 *
 * <p>Also publishes an {@link AcceptHeaderLocaleResolver} so the locale is picked from the
 * incoming {@code Accept-Language} header instead of {@code Locale.getDefault()}.</p>
 */
@AutoConfiguration
@EnableConfigurationProperties(I18nProperties.class)
public class CommonI18nAutoConfiguration {

    private final I18nProperties props;
    private MessageSource messageSource;

    public CommonI18nAutoConfiguration(I18nProperties props) {
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
        this.messageSource = source;
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

    @PostConstruct
    void wireStaticBridge() {
        Messages.setMessageSource(messageSource);
    }
}
