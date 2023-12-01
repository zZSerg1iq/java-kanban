package managers.task;

import managers.Managers;
import managers.task.impl.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return (InMemoryTaskManager) Managers.getDefault();
    }


}