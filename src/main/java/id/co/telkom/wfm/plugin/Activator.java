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
        //REGISTER PLUGIN HERE
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
        registrationList.add(context.registerService(ValidateVrf.class.getName(), new ValidateVrf(), null));
        registrationList.add(context.registerService(GenerateIPReservation.class.getName(), new GenerateIPReservation(), null));
        registrationList.add(context.registerService(GenerateUplinkPort.class.getName(), new GenerateUplinkPort(), null));
        registrationList.add(context.registerService(GenerateDownlinkPort.class.getName(), new GenerateDownlinkPort(), null));
        registrationList.add(context.registerService(UpdateTaskStatusMyStaff.class.getName(), new UpdateTaskStatusMyStaff(), null));
        registrationList.add(context.registerService(GenerateMeService.class.getName(), new GenerateMeService(), null));
        registrationList.add(context.registerService(GenerateMeAccess.class.getName(), new GenerateMeAccess(), null));
        registrationList.add(context.registerService(AbortOrder.class.getName(), new AbortOrder(), null));
        registrationList.add(context.registerService(GeneratePeName.class.getName(), new GeneratePeName(), null));
        
    }

    @Override
    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}
