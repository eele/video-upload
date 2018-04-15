# $1: uid; $2: vid
mkdir /usr/local/nginx/videofiles/$1/${2}"_"
ffmpeg -noautorotate -i /usr/local/nginx/videofiles/$1/$2 -y -acodec copy -vcodec copy /usr/local/nginx/videofiles/$1/${2}"_"/v.mp4
ffmpeg -noautorotate -i /usr/local/nginx/videofiles/$1/${2}"_"/v.mp4 -y -c copy -bsf:v h264_mp4toannexb /usr/local/nginx/videofiles/$1/${2}"_"/v.ts
ffmpeg -i /usr/local/nginx/videofiles/$1/${2}"_"/v.ts -y -c copy -map 0 -f segment -segment_list /usr/local/nginx/videofiles/$1/${2}"_"/play.m3u8 -segment_time 10 /usr/local/nginx/videofiles/$1/${2}"_"/output%03d.ts
rm -f /usr/local/nginx/videofiles/$1/${2}"_"/v.mp4 /usr/local/nginx/videofiles/$1/${2}"_"/v.ts
ffmpeg -ss 1 -i /usr/local/nginx/videofiles/$1/$2 -y -f image2  -vframes 1 -s "352x240" /usr/local/nginx/videofiles/$1/${2}"_"/img.jpg
rm -f /usr/local/nginx/videofiles/$1/$2