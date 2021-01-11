# ConentProviderExample
Creating custom content provider which to access database and share between other apps; 

AppProvider - custom content provider, managing basic crud operations.

AppDatabase - singleton class to create database, table... and upgrade it to new version.

XxxContract - a class for containing static constants and static methods for the content provider.

Don't forget to define the provider configurations on the Manifest.xml file.

<application>
  ...
  <provider
            android:name="me.modernpage.tasktimer.AppProvider"
            android:authorities="me.modernpage.tasktimer.AppProvider"
            android:exported="false"/>
  
  ...
</application>

Sample code to use the AppProvider:
```java
String[] projection = {TasksContract.Columns.TASKS_NAME, TasksContract.Columns.TASKS_DESCRIPTION};
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(TasksContract.CONTENT_URI,
                projection,
                null,
                null,
                TasksContract.Columns.TASKS_NAME);
        if(cursor != null) {
            Log.d(TAG, "onCreate: number of rows: " + cursor.getCount());

            while (cursor.moveToNext()) {
                for(int i=0; i<cursor.getColumnCount(); i++) {
                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + ": " + cursor.getString(i));
                }
                Log.d(TAG, "onCreate: ============================");
            }
            cursor.close();
        }
        
```
        
![alt text](https://github.com/ModerPage/ConentProviderExample/blob/master/28a9bba9ffa148f78947d8940c1cfa09.png?raw=true)

![alt text](https://github.com/ModerPage/ConentProviderExample/blob/master/0f90cba3f3f04d8d9bec2cbe14703968.png?raw=true)
