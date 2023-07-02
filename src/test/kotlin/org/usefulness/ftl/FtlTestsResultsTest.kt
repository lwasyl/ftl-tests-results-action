package org.usefulness.ftl

import okio.source
import org.usefulness.ftl.model.Stacktrace
import org.usefulness.ftl.model.TestResult
import org.usefulness.ftl.model.TestsSummary
import org.usefulness.ftl.parsing.parseFtlResults
import org.usefulness.ftl.transformations.shorten
import java.util.Stack
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class FtlTestsResultsTest {

    @Test
    fun parsingTest() {
        parseFtlResults(source = getTestFile("results-with-errors-and-flakes.xml")).let { results ->
            assertEquals(
                expected = TestsSummary(
                    suiteName = "",
                    tests = 11,
                    failures = 2,
                    flakes = 1,
                    errors = 0,
                    skipped = 0,
                    totalDuration = 231.482.seconds,
                ),
                actual = results.summary
            )

            assertContentEquals(
                expected = listOf("NoPackageTest#testMethod2"),
                actual = results.tests.filter { it.isFlaky }.map { it.shortName }
            )

            assertContentEquals(
                expected = listOf(
                    "com.example.foo.bar.BarTest#testMethod2",
                    "com.example.foo.bar.BarTest#testMethod5"
                ),
                actual = results.tests.filter { it.isFailed }.map { it.fqn }
            )

            assertEquals(
                expected = 6.1370000000000005.seconds,
                actual = results.tests.single { it.shortName == "BazTest#testMethod2" }.duration,
            )

            assertEquals(
                expected = sampleStacktrace,
                actual = (results.tests.single { it.shortName == "BarTest#testMethod2" }.result as TestResult.Failure).errors.first(),
            )
        }
    }

    @Test
    fun stacktraceShortenerTest() {
        assertEquals(
            expected = Stacktrace.fromString(
                string = """androidx.test.espresso.PerformException: Error performing 'single click - At Coordinates: 539, 1125 and precision: 16, 16' on view '(view is an instance of android.view.ViewGroup and has child matching: an instance of android.widget.TextView and view.getText() with or without transformation to match: is "Confirm")'.
                    at androidx.test.espresso.PerformException${'$'}Builder.build(PerformException.java:1)
                    at androidx.test.espresso.base.PerformExceptionHandler.handleSafely(PerformExceptionHandler.java:8)
                    at androidx.test.espresso.base.PerformExceptionHandler.handleSafely(PerformExceptionHandler.java:9)
                    at androidx.test.espresso.base.DefaultFailureHandler${'$'}TypedFailureHandler.handle(DefaultFailureHandler.java:4)
                Caused by: java.lang.RuntimeException: Failed to call observer method
                    at androidx.lifecycle.a${'$'}a.a(Unknown Source:74)
                    at androidx.lifecycle.ReflectiveGenericLifecycleObserver.b(Unknown Source:12)
                    at androidx.lifecycle.h${'$'}a.a(Unknown Source:32)
                    at androidx.lifecycle.h.a(Unknown Source:106)
                Caused by: android.content.res.Resources${'$'}NotFoundException: String resource ID #0x0
                    at android.content.res.Resources.getText(Resources.java:453)
                    at android.content.res.Resources.getString(Resources.java:546)
                    at android.content.Context.getString(Context.java:762)
                    at androidx.appcompat.widget.py7.h(Unknown Source:71)
                """.trimIndent()
            ),
            actual = sampleStacktrace.shorten(),
        )
    }
}

private fun getTestFile(fileName: String) =
    FtlTestsResultsTest::class.java.getResourceAsStream("/$fileName")!!.source()

private val sampleStacktrace = Stacktrace.fromString(
    string = """androidx.test.espresso.PerformException: Error performing 'single click - At Coordinates: 539, 1125 and precision: 16, 16' on view '(view is an instance of android.view.ViewGroup and has child matching: an instance of android.widget.TextView and view.getText() with or without transformation to match: is "Confirm")'.
        at androidx.test.espresso.PerformException${'$'}Builder.build(PerformException.java:1)
        at androidx.test.espresso.base.PerformExceptionHandler.handleSafely(PerformExceptionHandler.java:8)
        at androidx.test.espresso.base.PerformExceptionHandler.handleSafely(PerformExceptionHandler.java:9)
        at androidx.test.espresso.base.DefaultFailureHandler${'$'}TypedFailureHandler.handle(DefaultFailureHandler.java:4)
        at androidx.test.espresso.base.DefaultFailureHandler.handle(DefaultFailureHandler.java:5)
        at androidx.test.espresso.ViewInteraction.waitForAndHandleInteractionResults(ViewInteraction.java:8)
        at androidx.test.espresso.ViewInteraction.desugaredPerform(ViewInteraction.java:11)
        at androidx.test.espresso.ViewInteraction.perform(ViewInteraction.java:8)
        at com.example.foo.bar.BarTest.foo(BarTest:87)
        at com.example.foo.bar.BarTest.bar(BarTest:49)
        at com.example.foo.bar.BarTest.bazz(BarTest:47)
        at com.example.foo.bar.BarTest(BarTest:5)
        at com.example.foo.bar.BarTest(BarTest:47)
        ... 52 trimmed
    Caused by: java.lang.RuntimeException: Failed to call observer method
        at androidx.lifecycle.a${'$'}a.a(Unknown Source:74)
        at androidx.lifecycle.ReflectiveGenericLifecycleObserver.b(Unknown Source:12)
        at androidx.lifecycle.h${'$'}a.a(Unknown Source:32)
        at androidx.lifecycle.h.a(Unknown Source:106)
        at androidx.databinding.ViewDataBinding.w(Unknown Source:48)
        at androidx.appcompat.widget.y24.b(Unknown Source:190)
        at androidx.appcompat.widget.js2.h(Unknown Source:147)
        at androidx.databinding.ViewDataBinding.i(Unknown Source:18)
        at androidx.databinding.ViewDataBinding.j(Unknown Source:4)
        at androidx.databinding.ViewDataBinding${'$'}c.run(Unknown Source:54)
        at androidx.appcompat.widget.bt9.doFrame(Unknown Source:4)
        at android.view.Choreographer${'$'}CallbackRecord.run(Choreographer.java:1229)
        at android.view.Choreographer${'$'}CallbackRecord.run(Choreographer.java:1239)
        at android.view.Choreographer.doCallbacks(Choreographer.java:899)
        at android.view.Choreographer.doFrame(Choreographer.java:827)
        at android.view.Choreographer${'$'}FrameDisplayEventReceiver.run(Choreographer.java:1214)
        at android.os.Handler.handleCallback(Handler.java:942)
        at android.os.Handler.dispatchMessage(Handler.java:99)
        at androidx.test.espresso.base.Interrogator.loopAndInterrogate(Interrogator.java:14)
        at androidx.test.espresso.base.UiControllerImpl.loopUntil(UiControllerImpl.java:8)
        at androidx.test.espresso.base.UiControllerImpl.loopUntil(UiControllerImpl.java:1)
        at androidx.test.espresso.base.UiControllerImpl.injectMotionEvent(UiControllerImpl.java:5)
        at androidx.test.espresso.action.MotionEvents.sendUp(MotionEvents.java:7)
        at androidx.test.espresso.action.MotionEvents.sendUp(MotionEvents.java:1)
        at androidx.test.espresso.action.Tap.sendSingleTap(Tap.java:5)
        at androidx.test.espresso.action.Tap.-${'$'}${'$'}Nest${'$'}smsendSingleTap(Tap.java:0)
        at androidx.test.espresso.action.Tap${'$'}1.sendTap(Tap.java:3)
        at androidx.test.espresso.action.GeneralClickAction.perform(GeneralClickAction.java:6)
        at androidx.test.espresso.ViewInteraction${'$'}SingleExecutionViewAction.perform(ViewInteraction.java:2)
        at androidx.test.espresso.ViewInteraction.doPerform(ViewInteraction.java:25)
        at androidx.test.espresso.ViewInteraction.-${'$'}${'$'}Nest${'$'}mdoPerform(ViewInteraction.java:0)
        at androidx.test.espresso.ViewInteraction${'$'}1.call(ViewInteraction.java:7)
        at androidx.test.espresso.ViewInteraction${'$'}1.call(ViewInteraction.java:1)
        at java.util.concurrent.FutureTask.run(FutureTask.java:264)
        at android.os.Handler.handleCallback(Handler.java:942)
        at android.os.Handler.dispatchMessage(Handler.java:99)
        at android.os.Looper.loopOnce(Looper.java:201)
        at android.os.Looper.loop(Looper.java:288)
        at android.app.ActivityThread.main(ActivityThread.java:7872)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit${'$'}MethodAndArgsCaller.run(RuntimeInit.java:548)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:936)
    Caused by: android.content.res.Resources${'$'}NotFoundException: String resource ID #0x0
        at android.content.res.Resources.getText(Resources.java:453)
        at android.content.res.Resources.getString(Resources.java:546)
        at android.content.Context.getString(Context.java:762)
        at androidx.appcompat.widget.py7.h(Unknown Source:71)
        at androidx.databinding.ViewDataBinding.i(Unknown Source:18)
        at androidx.databinding.ViewDataBinding.j(Unknown Source:4)
        at androidx.databinding.ViewDataBinding${'$'}OnStartListener.onStart(Unknown Source:10)
        at java.lang.reflect.Method.invoke(Native Method)
        at androidx.lifecycle.a${'$'}a.a(Unknown Source:52)
        ... 41 more
    
    """.trimIndent()
)
