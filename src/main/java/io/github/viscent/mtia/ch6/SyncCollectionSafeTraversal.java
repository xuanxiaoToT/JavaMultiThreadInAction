package io.github.viscent.mtia.ch6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 清单 6-12 保障对外包装对象的遍历操作的线程安全
 * <p>
 * 这些同步集合的 iterator 方法返回的 Iterator 实例并不是线程安全的 。
 */
public class SyncCollectionSafeTraversal {

    final List<String> syncList = Collections.synchronizedList(new ArrayList<String>());

    //
    public void dump() {
        Iterator<String> iterator = syncList.iterator();
        synchronized (syncList) {
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        }
    }
}
