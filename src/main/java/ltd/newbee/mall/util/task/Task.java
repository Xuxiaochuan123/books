package ltd.newbee.mall.util.task;


import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class Task implements Delayed, Runnable {
    private final String id;
    private final long start;

    /**
     *
     * @param id 定时任务ID
     * @param delayInMilliseconds 延迟执行时间，单位/毫秒
     */
    public Task(String id, long delayInMilliseconds) {
        this.id = id;
        this.start = System.currentTimeMillis() + delayInMilliseconds;
    }

    public String getId() {
        return id;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = this.start - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.start - ((Task) o).start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Task t)) {
            return false;
        }
        return this.id.equals(t.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
