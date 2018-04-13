#
# Created by ice1000 on 2018-03-14
#

function main()

end

try
	throw runtime.exception("shit")
catch e
	system.out.println(e.what())
end
