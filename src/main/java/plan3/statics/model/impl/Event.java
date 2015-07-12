package plan3.statics.model.impl;

import plan3.statics.model.Location;

public interface Event {

    Location what();

    static interface Added extends Event {}

    static interface Updated extends Event {}

    static interface Deleted extends Event {}
}
