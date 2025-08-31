import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from '@/components/ui/sidebar';
import { NavGroup } from '@/components/layout/nav-group';
import { NavUser } from '@/components/layout/nav-user';
import { sidebarData } from './data/sidebar-data';
import { useAuthStore } from '@/stores/authStore';
import { useEffect } from 'react';
import { api } from '@/lib/api';

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const { user, setUser } = useAuthStore((s) => s.auth); // Destructure user and setUser

  useEffect(() => {
    const fetchUserRole = async () => {
      try {
        const response = await api.get('/api/account');
        const userData = response.data as any;
        const role = Array.isArray(userData?.authorities) ? userData.authorities : [];
        setUser({
          accountNo: userData?.login ?? '',
          email: userData?.email ?? '',
          firstName: userData?.firstName ?? '',
          lastName: userData?.lastName ?? '',
          role: role,
          exp: 0,
        });
        console.log('User role:', role);
        console.log('Sidebar data:', sidebarData);
      } catch (error) {
        console.error('Failed to fetch user role:', error);
      }
    };

    fetchUserRole();
  }, [setUser]);

  // Filter navGroups to exclude "Utilisateurs" if role does not include ROLE_ADMIN
  const filteredNavGroups = sidebarData.navGroups.map((group) => {
    if (group.title === 'Général') {
      return {
        ...group,
        items: group.items.filter((item) => 
          item.title !== 'Utilisateurs' || (user?.role?.includes('ROLE_ADMIN') ?? false)
        ),
      };
    }
    return group;
  });

  return (
    <Sidebar collapsible="icon" variant="floating" {...props}>
      <SidebarHeader>
        <div className="flex justify-center p-2">
          <img
            src="/images/municipality-logo.png"
            alt="Municipality Logo"
            className="h-10 w-auto"
          />
        </div>
      </SidebarHeader>
      <SidebarContent>
        {filteredNavGroups.map((props) => (
          <NavGroup key={props.title} {...props} />
        ))}
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={sidebarData.user} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  );
}