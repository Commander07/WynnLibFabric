package io.github.nbcss.wynnlib.gui.dicts.filter

import io.github.nbcss.wynnlib.items.BaseItem
import io.github.nbcss.wynnlib.gui.dicts.sorter.SorterGroup

abstract class SortersContainer<T: BaseItem>(memory: CriteriaState<T>,
                                             sorters: List<SorterGroup>): CriteriaGroup<T>(memory) {

    init {

    }

    class Builder {

    }
}