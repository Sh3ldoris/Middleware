import com.hazelcast.config.Config;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.config.FileSystemYamlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.io.FileNotFoundException;
import java.io.IOException;


// Example application which is a Hazelcast cluster member
public class TaskMember {

	public static void main(String[] args) {
        // The command-line argument is a prefix for entries created by this member
		if (args.length != 1) {
			System.err.println("Usage: bash run-member.sh <prefix>");
			return;
		}
		String prefix = args[0];

		try {
            Config config = new FileSystemYamlConfig("hazelcast.yaml");
            HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance(config);

            // Keep the member running until enter is pressed
            try {
                System.out.println("Press enter to exit");
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Leave the cluster
            hazelcast.shutdown();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
