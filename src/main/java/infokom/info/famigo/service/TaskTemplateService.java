package infokom.info.famigo.service;

import infokom.info.famigo.entity.TaskTemplate;
import infokom.info.famigo.repository.TaskTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskTemplateService {

    private final TaskTemplateRepository taskTemplateRepository;

    public TaskTemplateService(TaskTemplateRepository taskTemplateRepository) {
        this.taskTemplateRepository = taskTemplateRepository;
    }

    public List<TaskTemplate> findAll() {
        return taskTemplateRepository.findAll();
    }
}
