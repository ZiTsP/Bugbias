package bugbias.main.core.revision;

import java.util.List;
import java.util.Optional;

import bugbias.main.internal.TreeData;
import bugbias.main.widget.IOutputView;
import bugbias.main.widget.RevisionTable;

public interface IAnalyseRevisionBridge {

    public Optional<TreeData> analyseLastRevision();

    public Optional<TreeData> analyseCurrentRevision();
    
    public Optional<TreeData> analyseRevision(Object index);
    
    public void learn(List<Object> indexList, boolean isAccurate);
    
    public void setOutput(IOutputView output);
    
    public void setConfig(RevAnalyseConfig config);

    public void setWidget(RevisionTable widget);
    
    public Optional<Object> getLastRevision();

    
}
