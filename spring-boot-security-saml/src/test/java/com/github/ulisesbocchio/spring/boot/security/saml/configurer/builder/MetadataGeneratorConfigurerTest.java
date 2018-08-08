package com.github.ulisesbocchio.spring.boot.security.saml.configurer.builder;

import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderBuilder;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderEndpoints;
import com.github.ulisesbocchio.spring.boot.security.saml.properties.MetadataGeneratorProperties;
import com.github.ulisesbocchio.spring.boot.security.saml.properties.SAMLSSOProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.saml.metadata.*;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Ulises Bocchio
 */
public class MetadataGeneratorConfigurerTest {

    private ServiceProviderBuilder builder;
    private MetadataGeneratorProperties metadataGeneratorConfig;
    private ServiceProviderEndpoints serviceProviderEndpoints;
    private SAMLSSOProperties properties;
    private ExtendedMetadata extendedMetadata;
    private MetadataManager metadataManager;

    @Before
    public void setup() {
        properties = mock(SAMLSSOProperties.class);
        metadataGeneratorConfig = spy(new MetadataGeneratorProperties());
        serviceProviderEndpoints = spy(new ServiceProviderEndpoints());
        extendedMetadata = spy(new ExtendedMetadata());
        metadataManager = mock(MetadataManager.class);
        doReturn(new ArrayList<>()).when(metadataManager).getAvailableProviders();
        when(properties.getMetadataGenerator()).thenReturn(metadataGeneratorConfig);
        builder = mock(ServiceProviderBuilder.class);
        when(builder.getSharedObject(ServiceProviderEndpoints.class)).thenReturn(serviceProviderEndpoints);
        when(builder.getSharedObject(SAMLSSOProperties.class)).thenReturn(properties);
        when(builder.getSharedObject(ExtendedMetadata.class)).thenReturn(extendedMetadata);
        when(builder.getSharedObject(MetadataManager.class)).thenReturn(metadataManager);
    }

    @Test
    public void init() throws Exception {
        MetadataGeneratorConfigurer configurer = new MetadataGeneratorConfigurer();
        configurer.init(builder);
        verify(builder).getSharedObject(eq(ServiceProviderEndpoints.class));
        verify(builder).getSharedObject(eq(SAMLSSOProperties.class));
        verify(properties).getMetadataGenerator();
    }

    @Test
    public void configure_defaults() throws Exception {
        MetadataGeneratorConfigurer configurer = spy(new MetadataGeneratorConfigurer());
        configurer.init(builder);
        configurer.configure(builder);
        ArgumentCaptor<MetadataDisplayFilter> metadataDisplayFilterCaptor = ArgumentCaptor.forClass(MetadataDisplayFilter.class);
        ArgumentCaptor<MetadataGenerator> metadataGeneratorCaptor = ArgumentCaptor.forClass(MetadataGenerator.class);
        ArgumentCaptor<MetadataGeneratorFilter> metadataGeneratorFilterCaptor = ArgumentCaptor.forClass(MetadataGeneratorFilter.class);
        verify(builder).setSharedObject(eq(MetadataDisplayFilter.class), metadataDisplayFilterCaptor.capture());
        verify(builder).setSharedObject(eq(MetadataGenerator.class), metadataGeneratorCaptor.capture());
        verify(builder).setSharedObject(eq(MetadataGeneratorFilter.class), metadataGeneratorFilterCaptor.capture());
        MetadataDisplayFilter displayFilter = metadataDisplayFilterCaptor.getValue();
        MetadataGenerator generator = metadataGeneratorCaptor.getValue();
        MetadataGeneratorFilter generatorFilter = metadataGeneratorFilterCaptor.getValue();
        assertThat(displayFilter.getFilterProcessesUrl()).isEqualTo(metadataGeneratorConfig.getMetadataUrl());
        assertThat(serviceProviderEndpoints.getMetadataURL()).isEqualTo(metadataGeneratorConfig.getMetadataUrl());
        assertThat(generator.getAssertionConsumerIndex()).isEqualTo(metadataGeneratorConfig.getAssertionConsumerIndex());
        assertThat(generator.getBindingsHoKSSO()).containsExactlyElementsOf(metadataGeneratorConfig.getBindingsHokSso());
        assertThat(generator.getBindingsSLO()).containsExactlyElementsOf(metadataGeneratorConfig.getBindingsSlo());
        assertThat(generator.getBindingsSSO()).containsExactlyElementsOf(metadataGeneratorConfig.getBindingsSso());
        assertThat(generator.getEntityBaseURL()).isEqualTo(metadataGeneratorConfig.getEntityBaseUrl());
        assertThat(generator.getEntityId()).isEqualTo(metadataGeneratorConfig.getEntityId());
        assertThat(generator.getNameID()).containsExactlyElementsOf(metadataGeneratorConfig.getNameId());
        assertThat(generator.getId()).isEqualTo(metadataGeneratorConfig.getId());
        assertThat(generator.isWantAssertionSigned()).isEqualTo(metadataGeneratorConfig.isWantAssertionSigned());
        assertThat(generator.isIncludeDiscoveryExtension()).isEqualTo(metadataGeneratorConfig.isIncludeDiscoveryExtension());
        assertThat(generator.isRequestSigned()).isEqualTo(metadataGeneratorConfig.isRequestSigned());
        assertThat(generatorFilter).isNotNull();
    }

    @Test
    public void configure_arguments() throws Exception {
        MetadataGeneratorConfigurer configurer = spy(new MetadataGeneratorConfigurer());
        configurer
                .metadataURL("/metadata")
                .assertionConsumerIndex(999)
                .bindingsHoKSSO("pepe", "toto")
                .bindingsSLO("chico", "pompi")
                .bindingsSSO("tito", "coco")
                .entityBaseURL("/base")
                .entityId("entityId")
                .nameId("name", "rulo")
                .id("id")
                .wantAssertionSigned(false)
                .includeDiscoveryExtension(false)
                .requestSigned(true);
        configurer.init(builder);
        configurer.configure(builder);
        ArgumentCaptor<MetadataDisplayFilter> metadataDisplayFilterCaptor = ArgumentCaptor.forClass(MetadataDisplayFilter.class);
        ArgumentCaptor<MetadataGenerator> metadataGeneratorCaptor = ArgumentCaptor.forClass(MetadataGenerator.class);
        ArgumentCaptor<MetadataGeneratorFilter> metadataGeneratorFilterCaptor = ArgumentCaptor.forClass(MetadataGeneratorFilter.class);
        verify(builder).setSharedObject(eq(MetadataDisplayFilter.class), metadataDisplayFilterCaptor.capture());
        verify(builder).setSharedObject(eq(MetadataGenerator.class), metadataGeneratorCaptor.capture());
        verify(builder).setSharedObject(eq(MetadataGeneratorFilter.class), metadataGeneratorFilterCaptor.capture());
        MetadataDisplayFilter displayFilter = metadataDisplayFilterCaptor.getValue();
        MetadataGenerator generator = metadataGeneratorCaptor.getValue();
        MetadataGeneratorFilter generatorFilter = metadataGeneratorFilterCaptor.getValue();
        assertThat(displayFilter.getFilterProcessesUrl()).isEqualTo("/metadata");
        assertThat(serviceProviderEndpoints.getMetadataURL()).isEqualTo("/metadata");
        assertThat(generator.getAssertionConsumerIndex()).isEqualTo(999);
        assertThat(generator.getBindingsHoKSSO()).containsExactly("pepe", "toto");
        assertThat(generator.getBindingsSLO()).containsExactly("chico", "pompi");
        assertThat(generator.getBindingsSSO()).containsExactly("tito", "coco");
        assertThat(generator.getEntityBaseURL()).isEqualTo("/base");
        assertThat(generator.getEntityId()).isEqualTo("entityId");
        assertThat(generator.getNameID()).containsExactly("name", "rulo");
        assertThat(generator.getId()).isEqualTo("id");
        assertThat(generator.isWantAssertionSigned()).isEqualTo(false);
        assertThat(generator.isIncludeDiscoveryExtension()).isEqualTo(false);
        assertThat(generator.isRequestSigned()).isEqualTo(true);
        assertThat(generatorFilter).isNotNull();
    }
}