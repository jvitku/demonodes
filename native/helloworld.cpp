#include <iostream>
#include <fstream>           
#include <ctime>
/**
*
* Simple demo C++ applicaiton which prints out the corrent date and time and prints message each second.
*
* The same is also logged in the file output.txt (because Nengoros does not shouw STDOUT of slave processes by default).
*
* author Jaroslav Vitku
*/

using namespace std;

// Get current date/time, format is YYYY-MM-DD.HH:mm:ss
// http://stackoverflow.com/questions/997946/how-to-get-current-time-and-date-in-c
const std::string currentDateTime() {
	time_t     now = time(0);
	struct tm  tstruct;
	char       buf[80];
	tstruct = *localtime(&now);
	// Visit http://en.cppreference.com/w/cpp/chrono/c/strftime
	// for more information about date/time format
	strftime(buf, sizeof(buf), "%Y-%m-%d.%X", &tstruct);
	return buf;
}


int main()
{
	ofstream outFile;         
	// delete old contents of the file
	outFile.open("out_helloworld.txt",ios::out);
	if (outFile.fail())     
	{
		cout << "Error opening \"out_helloworld.txt\" for writing.\n";
		return 1;
	}
	outFile << "\nHello World! I am C++ application from demonodes/native/! Will write to the out_helloworld.txt file! Now is: "
		<< currentDateTime() << endl;
	cout << "\nHello World! I am C++ application demonodes/native/! Will write to the out_helloworld.txt file! Now is: "
		<< currentDateTime() << endl;
	outFile.close();

	struct timespec tp;
	tp.tv_sec = 1; 		//seconds so sleep for
	tp.tv_nsec = 1000; 	//nano seconds to sleep for
	int poc =0;

	while(1){
		// append to file the following and sleep one second..
		outFile.open("out_helloworld.txt",ios::app);
		cout << " running for " << poc++ << " sec"<< endl;
		outFile << " running for " << poc << " sec"<< endl;
		outFile.close();    

		nanosleep(&tp, NULL);
	}
	return 0;
}
