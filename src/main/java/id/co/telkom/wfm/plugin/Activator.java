package id.co.telkom.wfm.plugin;

import java.util.ArrayList;
import java.util.Collection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    protected Collection<ServiceRegistration> registrationList;

    @Override
    public void start(BundleContext context) {
        registrationList = new ArrayList<>();
        //DEFAULT APPLICATION PLUGIN
        registrationList.add(context.registerService(RevisedTask.class.getName(), new RevisedTask(), null));
        //TEST PLUGIN
        registrationList.add(context.registerService(TestGenerateEbis.class.getName(), new TestGenerateEbis(), null));
        registrationList.add(context.registerService(TestUpdateStatusEbis.class.getName(), new TestUpdateStatusEbis(), null));
        //REGISTER PLUGIN HERE
        registrationList.add(context.registerService(GenerateWonumEbis.class.getName(), new GenerateWonumEbis(), null));
        registrationList.add(context.registerService(UpdateTaskStatusEbis.class.getName(), new UpdateTaskStatusEbis(), null));
        registrationList.add(context.registerService(CpeValidationEbis.class.getName(), new CpeValidationEbis(), null));
        registrationList.add(context.registerService(UpdateAssignmentEbis.class.getName(), new UpdateAssignmentEbis(), null));
        registrationList.add(context.registerService(FalloutIncident.class.getName(), new FalloutIncident(), null));
        registrationList.add(context.registerService(UpdateTaskStatusMyStaff.class.getName(), new UpdateTaskStatusMyStaff(), null));
        registrationList.add(context.registerService(AbortOrder.class.getName(), new AbortOrder(), null));
        registrationList.add(context.registerService(GenerateFallout.class.getName(), new GenerateFallout(), null));
        registrationList.add(context.registerService(IntegrationFallout.class.getName(), new IntegrationFallout(), null));
    }

    @Override
    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}
