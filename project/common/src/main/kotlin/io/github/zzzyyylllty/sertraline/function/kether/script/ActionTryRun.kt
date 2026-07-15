package io.github.zzzyyylllty.sertraline.function.kether.script

import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.ConcurrentHashMap

class ActionTryRun(
    private val tryBody: ParsedAction<*>,
    private val catchBody: ParsedAction<*>?,
    private val finallyBody: ParsedAction<*>?,
    private val exceptionType: String?
) : ScriptAction<Any?>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Any?> {
        val future = CompletableFuture<Any?>()

        frame.run(tryBody).whenComplete { result, throwable ->
            if (throwable != null) {
                handleException(frame, throwable, future)
            } else {
                handleFinally(frame, result, null, future)
            }
        }

        return future
    }

    private fun handleException(
        frame: ScriptFrame,
        throwable: Throwable,
        future: CompletableFuture<Any?>
    ) {
        val ex = if (throwable is CompletionException && throwable.cause != null) {
            throwable.cause!!
        } else {
            throwable
        }

        if (catchBody != null && (exceptionType == null || matchesType(ex, exceptionType))) {
            frame.variables().set("exception", ex)
            frame.run(catchBody).whenComplete { catchResult, catchError ->
                handleFinally(frame, catchResult, catchError, future)
            }
        } else {
            handleFinally(frame, null, throwable, future)
        }
    }

    private fun handleFinally(
        frame: ScriptFrame,
        result: Any?,
        error: Throwable?,
        future: CompletableFuture<Any?>
    ) {
        if (finallyBody != null) {
            frame.run(finallyBody).whenComplete { _, _ ->
                if (error != null) future.completeExceptionally(error)
                else future.complete(result)
            }
        } else {
            if (error != null) future.completeExceptionally(error)
            else future.complete(result)
        }
    }

    companion object {
        private val classCache = ConcurrentHashMap<String, Class<*>>().apply {
            put("java.lang.Throwable", Throwable::class.java)
            put("java.lang.Exception", Exception::class.java)
            put("java.lang.RuntimeException", RuntimeException::class.java)
            put("java.lang.Error", Error::class.java)
            put("java.lang.NullPointerException", NullPointerException::class.java)
            put("java.lang.IllegalArgumentException", IllegalArgumentException::class.java)
            put("java.lang.IllegalStateException", IllegalStateException::class.java)
            put("java.lang.IndexOutOfBoundsException", IndexOutOfBoundsException::class.java)
            put("java.lang.ArithmeticException", ArithmeticException::class.java)
            put("java.lang.ClassCastException", ClassCastException::class.java)
            put("java.util.concurrent.CompletionException", CompletionException::class.java)
        }

        private fun resolveClass(typeName: String): Class<*>? {
            return classCache.getOrPut(typeName) {
                try {
                    Class.forName(typeName)
                } catch (_: ClassNotFoundException) {
                    null
                }
            }
        }

        private fun matchesType(exception: Throwable, typeName: String): Boolean {
            val clazz = resolveClass(typeName) ?: return false
            return clazz.isInstance(exception)
        }

        @KetherParser(["try-run", "tryrun"], shared = true)
        fun parser() = scriptParser {
            val tryBody = it.nextParsedAction()

            // optional catch
            var exceptionType: String? = null
            var catchBody: ParsedAction<*>? = null
            try {
                it.mark()
                it.expect("catch")
                if (it.hasNext()) {
                    it.mark()
                    val next = it.nextToken()
                    if (next == "{") {
                        it.reset()
                    } else {
                        exceptionType = next
                    }
                }
                catchBody = it.nextParsedAction()
            } catch (_: Exception) {
                it.reset()
            }

            // optional finally
            var finallyBody: ParsedAction<*>? = null
            try {
                it.mark()
                it.expect("finally")
                finallyBody = it.nextParsedAction()
            } catch (_: Exception) {
                it.reset()
            }

            ActionTryRun(tryBody, catchBody, finallyBody, exceptionType)
        }
    }
}
