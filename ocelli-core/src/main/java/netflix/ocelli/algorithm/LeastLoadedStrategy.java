package netflix.ocelli.algorithm;

import java.util.ArrayList;
import java.util.List;

import netflix.ocelli.ManagedClient;
import netflix.ocelli.WeightingStrategy;
import netflix.ocelli.metrics.CoreClientMetrics;
import netflix.ocelli.selectors.ClientsAndWeights;

public class LeastLoadedStrategy<Host, Client> implements WeightingStrategy<Host, Client> {
    
    @Override
    public ClientsAndWeights<Client> call(List<ManagedClient<Host, Client>> source) {
        List<Client>  clients = new ArrayList<Client>(source.size());
        List<Integer> weights = new ArrayList<Integer>();
        Integer max = 0;
        for (ManagedClient<Host, Client> context : source) {
            clients.add(context.getClient());
            
            int cur = (int) context.getMetrics(CoreClientMetrics.class).getPendingRequestCount();
            if (cur > max) 
                max = cur;
            weights.add(cur);
        }

        int count = 0;
        for (int i = 0; i < weights.size(); i++) {
            count += max - weights.get(i) + 1;
            weights.set(i, count);
        }
        System.out.println(weights);
        return new ClientsAndWeights<Client>(clients, null);
    }
}
