package com.ies.blelib.device;

import java.util.HashMap;

import com.ies.blelib.service.BaseService;
import com.ies.blelib.service.GapService;
import com.ies.blelib.service.GattService;
import com.ies.blelib.service.TIDeviceInfoService;

public class TISensorTagDevice {
    private static HashMap<String, BaseService> SERVICES = 
            new HashMap<String, BaseService>();

    static {
        final GattService gapSerivce = new GattService();
        final GapService gattSerivce = new GapService();
        final TIDeviceInfoService deviceInfoSerivce = new TIDeviceInfoService();

        SERVICES.put(gapSerivce.getUUID(), gapSerivce);
        SERVICES.put(gattSerivce.getUUID(), gattSerivce);
        SERVICES.put(deviceInfoSerivce.getUUID(), deviceInfoSerivce);
    }

    public static BaseService getService(String uuid) {
        return SERVICES.get(uuid);
    }
}
