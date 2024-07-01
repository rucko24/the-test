package org.example;

import org.example.util.GetOperatingSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetOperatingSystemTest {

    @Test
    @DisplayName("afafa")
    void test() {
        GetOperatingSystem os = GetOperatingSystem.getOsInfo();

        if (os == GetOperatingSystem.WINDOWS) {

            assertThat(os.getOsName()).contains(GetOperatingSystem.WINDOWS.getOsName());

        } else if (os == GetOperatingSystem.LINUX) {

            assertThat(os.getOsName()).contains(GetOperatingSystem.LINUX.getOsName());

        } else if (os == GetOperatingSystem.MAC) {

            assertThat(os.getOsName()).contains(GetOperatingSystem.MAC.getOsName());

        } else if (os == GetOperatingSystem.FREEBSD) {

            assertThat(os.getOsName()).contains(GetOperatingSystem.FREEBSD.getOsName());

        } else {

            assertThat(os.getOsName()).contains(GetOperatingSystem.OTHER.getOsName());
        }
    }

}
