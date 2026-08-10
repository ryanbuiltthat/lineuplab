# Keep kotlinx.serialization serializers for formation slot JSON stored in Room.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class com.lineuplab.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.lineuplab.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
