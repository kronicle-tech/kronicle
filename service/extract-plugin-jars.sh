rm -rf extracted-plugins/

for z in plugins/*.jar; do
  extract_dir="extracted-plugins/${z##*/}"
  mkdir -p $extract_dir
  unzip -qn "$z" -d $extract_dir
done

echo Plugins Disk Usage
echo ==================
du -h -d 4 extracted-plugins | sort -rh
