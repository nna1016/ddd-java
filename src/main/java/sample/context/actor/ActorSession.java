package sample.context.actor;

/**
 * The actor session of the thread local scope.
 */
public class ActorSession {
    private static final ThreadLocal<Actor> actorLocal = new ThreadLocal<>();

    /** Relate a actor with a actor session. */
    public static void bind(final Actor actor) {
        actorLocal.set(actor);
    }

    /** Unbind a actor session. */
    public static void unbind() {
        actorLocal.remove();
    }

    /**
     * Return an effective actor. When You are not related, an anonymous is
     * returned.
     */
// ThreadLocalとは、スレッド（リクエスト）毎に共有されずメモリに格納できる変数
// ThreadLocalを使っているので、actorLocalに値が入っていない場合はActor.Anonymousを返す
    public static Actor actor() {
        Actor actor = actorLocal.get();
        return actor != null ? actor : Actor.Anonymous;
    }

}
