use strict;
use warnings;
use Getopt::Long;
use Cwd 'abs_path'; # To get absolute path for venv

# Function to kill process on a given port
sub kill_process_on_port {
    my ($port) = @_;
    print "Checking for processes on port $port...\n";
    my $pid = `lsof -t -i :$port`;
    chomp $pid;

    if ($pid) {
        print "Found process with PID $pid on port $port. Killing it...\n";
        system("kill -9 $pid");
        if ($? == 0) {
            print "Process $pid killed successfully.\n";
        } else {
            warn "Failed to kill process $pid: $!\n";
        }
    } else {
        print "No process found on port $port.\n";
    }
}

# Detect OS
my $os = $^O;

# Default flag
my $javafx_flag = "";
my $venv_path = "";
my $run_server = 0; # Flag to indicate if the Python server should be run

GetOptions(
    "venv=s" => \$venv_path,
    "run-server" => \$run_server,
) or die "Error in command line arguments\n\nUsage: perl run.pl [OPTIONS]\nOptions:\n  --venv PATH       Path to Python virtual environment\n  --run-server      Start the Python gRPC server\n";

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

# --- Python Server Setup ---
if ($run_server) {
    # Kill any existing process on port 50051 before starting the server
    kill_process_on_port(50051);

    my $python_server_script = "server.py";
    my $python_command = "";

    if ($venv_path) {
        my $abs_venv_path = abs_path($venv_path);
        if (! -d "$abs_venv_path/bin") {
            die "Error: Virtual environment not found at $abs_venv_path or 'bin' directory is missing.\n";
        }
        print "Using Python virtual environment: $abs_venv_path\n";
        $python_command = "source $abs_venv_path/bin/activate && python $python_server_script";
    } else {
        print "No virtual environment specified. Using system Python.\n";
        $python_command = "python $python_server_script";
    }

    chdir 'model/proto_server' or die "Failed to change directory to model/proto_server: $!";


    print "Starting Python gRPC server in background...\n";
    # Using 'nohup' and '&' to run in background and detach from terminal
    # Redirecting stdout/stderr to files to prevent zombie processes and keep terminal clean
    system("nohup bash -c '$python_command' > logs/python-server.log 2>&1 &") == 0
        or die "Failed to start Python gRPC server: $!";

    chdir '../..' or die "Failed to change directory: $!";

    print "Python gRPC server started. Output redirected to python-server.log.\n";
    print "To stop the server manually, find its process ID (e.g., 'ps aux | grep server.py') and kill it.\n";
    sleep 2; # Give the server a moment to start
}
# --- End Python Server Setup ---


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