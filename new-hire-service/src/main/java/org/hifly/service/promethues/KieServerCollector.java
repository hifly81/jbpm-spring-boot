package org.hifly.service.promethues;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.task.model.TaskSummary;

import java.util.*;

public class KieServerCollector extends Collector {

    private static final List<String> LABEL_NAMES = Collections.singletonList("unit");

    private RuntimeDataService runtimeDataService;

    public KieServerCollector(RuntimeDataService runtimeDataService) {
      this.runtimeDataService = runtimeDataService;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> metrics = new ArrayList<>();

        metrics.addAll(getProcessMetrics());
        metrics.addAll(getTasksMetrics());

        return metrics;
    }

    private List<MetricFamilySamples> getProcessMetrics() {

        return Arrays.asList(
                createCounter(
                        "kie_server_process_instances",
                        "Number of process instances",
                        "process_instances_count",
                        runtimeDataService.getProcessInstances(null) == null? 0
                                : runtimeDataService.getProcessInstances(null).size()
                ),
                createCounter(
                        "kie_server_process_instances_active",
                        "Number of process instances active",
                        "process_instances_active_count",
                        runtimeDataService.getProcessInstances(Arrays.asList(1), null, null) == null? 0
                                : runtimeDataService.getProcessInstances(Arrays.asList(1), null, null).size()
                ),
                createCounter(
                        "kie_server_process_instances_pending",
                        "Number of process instances pending",
                        "process_instances_pending_count",
                        runtimeDataService.getProcessInstances(Arrays.asList(0), null, null) == null? 0
                                : runtimeDataService.getProcessInstances(Arrays.asList(0), null, null).size()

                ),
                createCounter(
                        "kie_server_process_instances_suspended",
                        "Number of process instances suspended",
                        "process_instances_suspended_count",
                        runtimeDataService.getProcessInstances(Arrays.asList(4), null, null) == null? 0
                                : runtimeDataService.getProcessInstances(Arrays.asList(4), null, null).size()

                ),
                createCounter(
                        "kie_server_process_instances_aborted",
                        "Number of process instances aborted",
                        "process_instances_aborted_count",
                        runtimeDataService.getProcessInstances(Arrays.asList(3), null, null) == null? 0
                                : runtimeDataService.getProcessInstances(Arrays.asList(3), null, null).size()

                ),
                createCounter(
                        "kie_server_process_instances_completed",
                        "Number of process instances completed",
                        "process_instances_completed_count",
                        runtimeDataService.getProcessInstances(Arrays.asList(2), null, null) == null? 0
                                : runtimeDataService.getProcessInstances(Arrays.asList(2), null, null).size()

                )
        );
    }

    private List<MetricFamilySamples> getTasksMetrics() {
        double created = 0;
        double ready = 0;
        double reserved = 0;
        double inprogress = 0;
        double suspended = 0;
        double completed = 0;
        double failed = 0;
        double error = 0;
        double exited = 0;
        double obsolete = 0;

        List<TaskSummary> resultTasks =  new ArrayList<>();
        Collection<ProcessInstanceDesc> processInstanceDescs = runtimeDataService.getProcessInstances(null);
        if(processInstanceDescs != null && !processInstanceDescs.isEmpty()) {
            for(ProcessInstanceDesc processInstanceDesc: processInstanceDescs)
                resultTasks.addAll(runtimeDataService.getTasksByStatusByProcessInstanceId(processInstanceDesc.getId(), null, null));
        }

        if(!resultTasks.isEmpty()) {
            for(TaskSummary taskSummary: resultTasks) {
                switch (taskSummary.getStatus()) {
                    case Created:
                        created = created+1;
                        break;
                    case Ready:
                        ready = ready+1;
                        break;
                    case Reserved:
                        reserved = reserved+1;
                        break;
                    case InProgress:
                        inprogress = inprogress+1;
                        break;
                    case Suspended:
                        suspended = suspended+1;
                        break;
                    case Completed:
                        completed = completed+1;
                        break;
                    case Failed:
                        failed = failed+1;
                        break;
                    case Error:
                        error = error+1;
                        break;
                    case Exited:
                        exited = exited+1;
                        break;
                    case Obsolete:
                        obsolete = obsolete+1;
                        break;

                }
            }
        }


        return Arrays.asList(
                createCounter(
                        "kie_server_tasks",
                        "Number of tasks",
                        "tasks_count",
                        resultTasks.size()
                ),
                createCounter(
                        "kie_server_tasks_created",
                        "Number of created tasks",
                        "tasks_created_count",
                        created
                ),
                createCounter(
                        "kie_server_tasks_ready",
                        "Number of ready tasks",
                        "tasks_ready_count",
                        ready
                ),
                createCounter(
                        "kie_server_tasks_reserved",
                        "Number of reserved tasks",
                        "tasks_reserved_count",
                        reserved
                ),
                createCounter(
                        "kie_server_tasks_inprogress",
                        "Number of inprogress tasks",
                        "tasks_inprogress_count",
                        inprogress
                ),
                createCounter(
                        "kie_server_tasks_suspended",
                        "Number of suspended tasks",
                        "tasks_suspended_count",
                        suspended
                ),
                createCounter(
                        "kie_server_tasks_completed",
                        "Number of completed tasks",
                        "tasks_completed_count",
                        completed
                ),
                createCounter(
                        "kie_server_tasks_failed",
                        "Number of failed tasks",
                        "tasks_failed_count",
                        failed
                ),
                createCounter(
                        "kie_server_tasks_error",
                        "Number of error tasks",
                        "tasks_error_count",
                        error
                ),
                createCounter(
                        "kie_server_tasks_exited",
                        "Number of exited tasks",
                        "tasks_exited_count",
                        exited
                ),
                createCounter(
                        "kie_server_tasks_obsolete",
                        "Number of obsolete tasks",
                        "tasks_obsolete_count",
                        obsolete
                )
        );
    }

    private CounterMetricFamily createCounter(String metric, String help, String key, double value) {
        CounterMetricFamily metricFamily = new CounterMetricFamily(metric, help, LABEL_NAMES);
        metricFamily.addMetric(Collections.singletonList(key), value);
        return metricFamily;
    }
}
