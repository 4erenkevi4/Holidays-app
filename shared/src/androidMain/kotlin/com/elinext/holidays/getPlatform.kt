import com.elinext.holidays.AndroidPlatform
import com.elinext.holidays.Platform

actual fun getPlatform(): Platform {
    return AndroidPlatform()
}