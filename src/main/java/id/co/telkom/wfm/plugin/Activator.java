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
        //Register plugin here
        registrationList.add(context.registerService(GenerateWonumEbis.class.getName(), new GenerateWonumEbis(), null));
        registrationList.add(context.registerService(UpdateTaskStatusEbis.class.getName(), new UpdateTaskStatusEbis(), null));
        registrationList.add(context.registerService(GenerateStpNetLoc.class.getName(), new GenerateStpNetLoc(), null));
        registrationList.add(context.registerService(CpeValidationEbis.class.getName(), new CpeValidationEbis(), null));
        registrationList.add(context.registerService(TaskAttributeEbis.class.getName(), new TaskAttributeEbis(), null));
        registrationList.add(context.registerService(GenerateIpV4.class.getName(), new GenerateIpV4(), null));
        registrationList.add(context.registerService(UpdateAssignmentEbis.class.getName(), new UpdateAssignmentEbis(), null));
        registrationList.add(context.registerService(FalloutIncident.class.getName(), new FalloutIncident(), null));
        registrationList.add(context.registerService(GenerateSidConnectivity.class.getName(), new GenerateSidConnectivity(), null));
        registrationList.add(context.registerService(ValidateSto.class.getName(), new ValidateSto(), null));
        //Test Generate Ebis
        registrationList.add(context.registerService(TestGenerateEbis.class.getName(), new TestGenerateEbis(), null));
    }

    @Override
    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}
