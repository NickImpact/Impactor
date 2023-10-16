package net.impactdev.impactor.core.mail;

import net.impactdev.impactor.api.mail.MailService;
import net.impactdev.impactor.api.providers.ServiceProvider;
import net.impactdev.impactor.core.modules.ImpactorModule;

public final class MailModule implements ImpactorModule {

    @Override
    public void services(ServiceProvider provider) {
        provider.register(MailService.class, new ImpactorMailService());
    }
}
