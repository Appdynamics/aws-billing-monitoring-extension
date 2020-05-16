package com.appdynamics.extensions.aws.billing;

import com.amazonaws.services.cloudwatch.model.Metric;
import com.google.common.base.Predicates;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import java.util.List;


public class ServiceNamesPredicate implements Predicate<Metric> {

    private List<String> serviceNamesList;
    private Predicate<CharSequence> patternPredicate;

    public ServiceNamesPredicate(List<String> serviceNamesList){
        this.serviceNamesList = serviceNamesList;
        buildPattern();
    }

    private void buildPattern(){
        if (serviceNamesList != null && !serviceNamesList.isEmpty()) {
            for(String serviceName : serviceNamesList) {
                if(!Strings.isNullOrEmpty(serviceName)) {
                    Predicate<CharSequence> serviceNamePatternPredicate = Predicates.containsPattern(serviceName);
                    if (patternPredicate == null) {
                        patternPredicate = serviceNamePatternPredicate;
                    } else {
                        patternPredicate = Predicates.or(patternPredicate, serviceNamePatternPredicate);
                    }
                }
            }
        }
    }

    @Override
    public boolean apply(Metric metric) {
        if(patternPredicate == null){
            return true;
        }
        else{
            String serviceName = metric.getDimensions().get(0).getValue();
            return patternPredicate.apply(serviceName);
        }
    }
}
