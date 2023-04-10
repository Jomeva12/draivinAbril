package com.jomeva.driving.activities.modelos

class FCMResponse(
    var multicast_id: Long,
    var success: Int,
    var failure: Int,
    var canonical_ids: Int,
    results: ArrayList<Any>
) {
    var results = ArrayList<Any>()

    init {
        this.results = results
    }
}
