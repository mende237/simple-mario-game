# Detect OS
my $os = $^O;

# Default flag
my $javafx_flag = "";

if ($os eq 'linux') {
    my $session = $ENV{'XDG_SESSION_TYPE'} // 'unknown';

    if ($session eq 'wayland') {
        print "Wayland detected.\n";
        print "JavaFX on Wayland might have rendering issues or require specific configurations.\n";
        print "It is recommended to switch to an X11 session for optimal performance and compatibility.\n";
        exit 1;
    } else {
        print "X11 detected: using default rendering\n";
    }
} else {
    print "Windows detected: using default rendering\n";
}

# Build Maven command
my $cmd;

# Navigate to the mario directory
chdir 'mario' or die "Failed to change directory to mario: $!";

# First, clean and install the project to ensure all dependencies and generated sources are built
print "Building Mario project with: mvn clean install\n";
system("mvn clean install") == 0
    or die "Failed to build Mario project: $!";

$cmd = "mvn javafx:run";

print "Running: $cmd\n";

system($cmd) == 0
    or die "Failed to launch JavaFX app: $!";