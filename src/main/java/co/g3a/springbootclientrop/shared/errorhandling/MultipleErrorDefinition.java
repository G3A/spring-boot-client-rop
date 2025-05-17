package co.g3a.springbootclientrop.shared.errorhandling;

import java.util.List;

public interface MultipleErrorDefinition extends AppErrorDefinition {
    List<? extends AppErrorDefinition> errors();
}