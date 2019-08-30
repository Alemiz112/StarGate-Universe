package alemiz.sgu.tasks;

import alemiz.sgu.StarGateUniverse;
import cn.nukkit.scheduler.Task;

public class ResponseRemoveTask extends Task {

    private String uuid;

    public ResponseRemoveTask(String uuid){
        this.uuid = uuid;
    }
    @Override
    public void onRun(int i) {
        StarGateUniverse.getInstance().responses.remove(uuid);
    }
}
