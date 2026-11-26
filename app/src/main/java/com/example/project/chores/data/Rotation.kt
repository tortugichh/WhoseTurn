package com.example.project.chores.data

import java.util.TimeZone

private const val DAY_MILLIS = 24L * 60 * 60 * 1000

internal fun currentEpochDay(
    nowMillis: Long = System.currentTimeMillis(),
    zone: TimeZone = TimeZone.getDefault(),
): Long = Math.floorDiv(nowMillis + zone.getOffset(nowMillis), DAY_MILLIS)

internal fun List<Long>.nextAfter(current: Long?): Long? {
    if (isEmpty()) return null
    val index = indexOf(current)
    return if (index < 0) first() else this[(index + 1) % size]
}
